/**
 * The processor. Traverses the inputed flow graph using a temporal depth first
 * search to create PCs and pass them to the constraint solvers using the
 * argument.
 *
 * @author Scott Kausler, Andrew Harris
 */
package edu.boisestate.cs;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.boisestate.cs.Settings.ReportType;
import edu.boisestate.cs.Settings.SolverType;
import edu.boisestate.cs.automatonModel.AutomatonModelManager;
import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse;
import edu.boisestate.cs.automatonModel.Model_Acyclic_Inverse_Manager;
import edu.boisestate.cs.decider.Decider;
import edu.boisestate.cs.graph.InvDefaultDirectedGraph;
import edu.boisestate.cs.graph.PrintConstraint;
import edu.boisestate.cs.graph.SymbolicEdge;
import edu.boisestate.cs.reporting.MCReporter;
import edu.boisestate.cs.reporting.Reporter;
import edu.boisestate.cs.reporting.Reporter_Inverse;
import edu.boisestate.cs.reporting.Reporter_Inverse_BFS;
import edu.boisestate.cs.reporting.SATReporter;
import edu.boisestate.cs.solvers.*;
import edu.boisestate.cs.util.LambdaVoid1;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.*;

//import javax.swing.plaf.synth.SynthSeparatorUI;

@SuppressWarnings({ "unchecked" })
public class SolveMain {
	private static int initialBound = 0;
	private static int defaultBound = 3;

	private static boolean reduce = false;
	private static boolean debug = false;
	private static boolean header = true;

	private static String inputFile;
	private static Alphabet alpha;
	
	public static void main(String[] args) {

		Settings settings = CommandLine.processArgs(args);

		// ensure arguments processed properly before continuing
		if (settings == null) {
			return;
		}

		// Check if input is SMT2 file and convert if needed
		String graphFilePath = settings.getGraphFilePath();
		if (graphFilePath.endsWith(".smt2")) {
			try {
				graphFilePath = convertSmt2ToJson(graphFilePath);
				settings.setGraphFilePath(graphFilePath);
			} catch (IOException | InterruptedException e) {
				System.err.println("Failed to convert SMT2 file: " + e.getMessage());
				e.printStackTrace();
				return;
			}
		}
    if (graphFilePath.endsWith(".ser")) {
      try { ObjectInputStream ois = new ObjectInputStream(new FileInputStream(graphFilePath));
        AStrBenchmarkBundle bundle = (AStrBenchmarkBundle) ois.readObject();
        ois.close();
        InvDefaultDirectedGraph graph = bundle.graph;
        String alphabetString = bundle.alphabetString;
        printDebug("alpha:" + alphabetString);
        StringBuilder alph = new StringBuilder();
        for (int i = 0; i<alphabetString.length(); i++) {
          char c = alphabetString.charAt(i);
          alph.append(c);
          alph.append(",");
        }
        alph.deleteCharAt(alph.length() - 1);

        alpha = new Alphabet(alph.toString());
        initialBound = bundle.bound;
        reduce = true;
        debug = settings.getDebug();
        printDebug("Loaded Serialized Graph: alpha: " + alpha + ", bound: " + initialBound);
        run_Acyclic_Inverse_r3(graph);
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

		inputFile = settings.getGraphFilePath();
			
		inputFile = settings.getGraphFilePath();
		initialBound = settings.getInitialBoundingLength();
		
		/*
		 * If solver is inverse, we can ignore the reporter and automata types, load the 
		 * graph and run the acyclic inverse method. 
		 */
		if (settings.getSolverType() == SolverType.INVERSE) {
			debug = settings.getDebug();
			if (debug)printHeader(inputFile, initialBound, "Inverse", "Inverse", "Acyclic");
			reduce = true;
			InvDefaultDirectedGraph graph = (InvDefaultDirectedGraph) loadGraph(inputFile);
			printDebug("==========================================" + "GRAPH STATS" + "==========================================");
			printDebug("NUM CONSTRAINTS:\t" + graph.vertexSet().size());
			printDebug("NUM PREDICATES:\t\t" + graph.getPredicates().size());
			printDebug("NUM SYMBOLIC INPUTS:\t" + graph.getNumSymInputs());
			printDebug("MAX CONCRETE STRING LENGTH:\t" + initialBound);
//			initialBound = graph.boundLengthHeuristic();
//			printDebug("BOUND LENGTH HEURISTIC:\t" + initialBound);
			printDebug("========================================================================================================");
//			initialBound= initialBound + 3;// add some padding
			run_Acyclic_Inverse_r3(graph);


			/*
			 * The remaining types are the concrete and blank solvers, which currently use the non-typed classes.
			 */
		} else {

			// initialize components object
			Components components = new Components();

			// load constraint graph
			loadGraph(components, settings);

			// load alphabet
			loadAlphabet(components, settings);

			// load solver
			loadSolver(components, settings);

			// if graph or parser not loaded, abort program
			if (components.getGraph() == null || components.getSolver() == null) {
				return;
			}

			// load parser
			loadParser(components, settings);

			// load reporter
			loadReporter(components, settings);

			// if reporter not loaded, abort program
			if (components.getReporter() == null) {
				return;
			}

			// run reporter
			components.getReporter().run();

		} // end other solver type

	}


	/*
	 * loadAlphabet for concrete and blank solvers.
	 */
	private static void loadAlphabet(Components components, Settings settings) {

		// declare alphabet variable
		Alphabet alphabet = null;

		// if alphabet declared
		if (settings.getAlphabetDeclaration() != null) {

			// create alphabet from declaration
			alphabet = new Alphabet(settings.getAlphabetDeclaration());

			// if alphabet is not superset of minimal alphabet
			if (!alphabet.isSuperset(settings.getMinAlphabet())) {

				// reset alphabet to null
				alphabet = null;
			}
		}

		// if alphabet not already set
		if (alphabet == null) {

			// create alphabet from minimum required alphabet
			alphabet = new Alphabet(settings.getMinAlphabet());
		}

		// store alphabet
		components.setAlphabet(alphabet);

	}

	private static void loadGraph(Components components, final Settings settings) {
		// store graph as component
		LambdaVoid1<String> setMinAlphabet = new LambdaVoid1<String>() {
			@Override
			public void execute(String s) {
				settings.setMinAlphabet(s);
			}
		};
		components.setGraph(loadGraph(settings.getGraphFilePath(), setMinAlphabet));
	}

	/*
	 * loadGraph for the concrete and blank solvers, needs to be public for dotgenerator, methodcount and cleargraph classes
	 */
	public static DirectedGraph<PrintConstraint, SymbolicEdge> loadGraph(String graphPath,
			LambdaVoid1<String> setMinAlphabet) {

		// initialize graph object as null
		DirectedGraph<PrintConstraint, SymbolicEdge> graph = new DefaultDirectedGraph<>(SymbolicEdge.class);

		// create json object mapper
		ObjectMapper mapper = new ObjectMapper();

		// initialize json file object
		File graphFile = new File(graphPath);

		// initialize lists for processing
		Map<Integer, PrintConstraint> constraintMap = new HashMap<>();
		Map<PrintConstraint, List<Integer>> sourceConstraintMap = new HashMap<>();
		List<Map<String, Object>> edgeData = new LinkedList<>();

		try {

			// get graph data from json file
			Map<String, Object> graphData = mapper.readValue(graphFile, Map.class);

			// add alphabet data to settings
			Map<String, Object> alphabetData = (Map<String, Object>) graphData.get("alphabet");
			String minAlphabet = (String) alphabetData.get("declaration");
			setMinAlphabet.execute(minAlphabet);

			// get constraint data from graph data
			List<Map<String, Object>> vertexData = (List<Map<String, Object>>) graphData.get("vertices");
			// System.out.println("VD " + vertexData);
			for (Map<String, Object> obj : vertexData) {

				// get constraint vertex data
				int id = (Integer) obj.get("id");
				String actualValue = (String) obj.get("actualValue");
				int num = (Integer) obj.get("num");
				long timeStamp;
				try {
					timeStamp = (Long) obj.get("timeStamp");
				} catch (ClassCastException e) {
					timeStamp = (Integer) obj.get("timeStamp");
				}
				int type = (Integer) obj.get("type");
				String value = (String) obj.get("value");

				// create constraint from vertex data
				PrintConstraint constraint = new PrintConstraint(id, actualValue, num, timeStamp, type, value);

				// add constraint to map
				constraintMap.put(id, constraint);

				// get source constraint list and add to map
				List<Integer> sourceConstraints = (List<Integer>) obj.get("sourceConstraints");
				sourceConstraintMap.put(constraint, sourceConstraints);

				// get incoming edges
				List<Map<String, Object>> incomingEdges = (List<Map<String, Object>>) obj.get("incomingEdges");

				for (Map<String, Object> incomingEdge : incomingEdges) {

					// add to edge list
					incomingEdge.put("target", id);
					edgeData.add(incomingEdge);
				}
			}

			// set sourceConstraints for each constraint
			for (PrintConstraint constraint : sourceConstraintMap.keySet()) {

				// for each constraint id
				for (int id : sourceConstraintMap.get(constraint)) {

					// get source constraint
					PrintConstraint sourceConstraint = constraintMap.get(id);

					// set source constraint as source for current constraint
					constraint.setSource(sourceConstraint);
				}

				// add constraint to graph
				graph.addVertex(constraint);
			}

			for (Map<String, Object> obj : edgeData) {

				// get symbolic edge data
				int sourceId = (Integer) obj.get("source");
				PrintConstraint source = constraintMap.get(sourceId);
				int targetId = (Integer) obj.get("target");
				PrintConstraint target = constraintMap.get(targetId);
				String type = (String) obj.get("type");
				// System.out.println("src " + source);
				// System.out.println("trgt " + target);
				// create symbolic edge in graph from data
				SymbolicEdge edge = graph.addEdge(source, target);
				// System.out.println(edge);
				edge.setType(type);
			}

		} catch (IOException i) {
			i.printStackTrace();
		}

		// return graph
		return graph;
	}

	/*
	 * loadParser for the concrete and blank solvers
	 */
	private static void loadParser(Components components, Settings settings) {

		// create and store parser as component
		components.setParser(new Parser(components.getSolver(), settings.getDebug()));

	}

	/*
	 * loadReporter for the concrete and blank solvers
	 */
	private static void loadReporter(Components components, Settings settings) {

		// get values from settings
		Settings.ReportType reportType = settings.getReportType();
		boolean debug = settings.getDebug();
		DirectedGraph<PrintConstraint, SymbolicEdge> graph = components.getGraph();
		Parser parser = components.getParser();
		ExtendedSolver solver = components.getSolver();

		// initialize reporter as null
		Reporter reporter = null;

		if (reportType == Settings.ReportType.MODEL_COUNT) {

			// ensure solver is model count solver
			if (solver instanceof ModelCountSolver) {

				// cast solver
				ModelCountSolver mcSolver = (ModelCountSolver) solver;

				// create reporter from parameters
				reporter = new MCReporter(graph, parser, solver, debug, mcSolver);
			}

		} else if (reportType == Settings.ReportType.SAT) {

			// create reporter from parameters
			reporter = new SATReporter(graph, parser, solver, debug);
		}

		// store reporter
		components.setReporter(reporter);
	}

	/*
	 * loadSolver for use with concrete and blank solvers
	 */
	private static void loadSolver(Components components, Settings settings) {

		// get needed info from settings object
		Settings.SolverType selectedSolver = settings.getSolverType();
		Settings.ReportType reportType = settings.getReportType();
		int modelVersion = settings.getAutomatonModelVersion();
		int boundingLength = settings.getInitialBoundingLength();
		Alphabet alphabet = components.getAlphabet();

		// initialize extend solver as null
		ExtendedSolver solver = null;

		// create specified solver for parser
		if (selectedSolver == Settings.SolverType.BLANK) {

			solver = new BlankSolver();

		} else if (selectedSolver == Settings.SolverType.CONCRETE) {

			solver = new ConcreteSolver(alphabet, boundingLength);

		} else if (selectedSolver == Settings.SolverType.JSA) {

			// get model manager instance
			AutomatonModelManager modelManager = AutomatonModelManager.getInstance(alphabet, modelVersion,
					boundingLength);

			if (reportType == Settings.ReportType.SAT) {

				solver = new AutomatonModelSolver(modelManager, boundingLength);

			} else if (reportType == Settings.ReportType.MODEL_COUNT) {

				solver = new MCAutomatonModelSolver(modelManager, boundingLength);
			}

		}

		// store created solver
		components.setSolver(solver);
	}

	/*
	 * loadGraph for jsa and inverse solvers
	 */
	private static DirectedGraph<PrintConstraint, SymbolicEdge> loadGraph(String graphPath) {
		// initialize variables

		// init null graph object
		DirectedGraph<PrintConstraint, SymbolicEdge> graph = new DefaultDirectedGraph<>(SymbolicEdge.class);
		//eas just a shadow graph for now
		InvDefaultDirectedGraph graphExtra = new InvDefaultDirectedGraph(SymbolicEdge.class);

		// create json object mapper
		ObjectMapper mapper = new ObjectMapper();

		// init json file object
		File graphFile = new File(graphPath);

		// init lists for processing
		Map<Integer, PrintConstraint> constraintMap = new HashMap<>();
		Map<PrintConstraint, List<Integer>> sourceConstraintMap = new HashMap<>();
		List<Map<String, Object>> edgeData = new LinkedList<>();

		try {
			// get graph data from json file
			Map<String, Object> graphData = mapper.readValue(graphFile, Map.class);

			// add alphabet data to settings
			Map<String, Object> alphabetData = (Map<String, Object>) graphData.get("alphabet");
			String minAlphabet = (String) alphabetData.get("declaration");
			// make sure alphabet has numbers so integers can be processed
			String nums = "0-9";
			alpha = new Alphabet(minAlphabet + "," + nums);

			// determine initialBound
			if (initialBound == 0) {
				if (graphData.get("inputLength") != null) {
					int inputLength = (int) graphData.get("inputLength");
					initialBound = inputLength;
					System.out.println("[IGEN] Using initial bound from JSON file .. " + initialBound);
				} else {
					initialBound = defaultBound;
					printDebug("[IGEN] Using initial bound default ..." + initialBound);
				}
			}

			// get constraint data from graph data
			List<Map<String, Object>> vertexData = (List<Map<String, Object>>) graphData.get("vertices");

			// add constraints from file to constraintMap
			// for each vertex
			for (Map<String, Object> obj : vertexData) {
				// get constraint vertex data
				int id = (Integer) obj.get("id");
				String actualValue = (String) obj.get("actualValue");
				int num = (Integer) obj.get("num");
				long timeStamp;

				try {
					timeStamp = (Long) obj.get("timeStamp");
				} catch (ClassCastException e) {
					timeStamp = (Integer) obj.get("timeStamp");
				}

				int type = (Integer) obj.get("type");
				String value = (String) obj.get("value");

				if (value.contains("\"")||value.contains("<init>")) {
					String concrete = value.split("!:!")[0];
					concrete = concrete.replace("\"","");
					int conc_string_length = concrete.length();
					if (initialBound < conc_string_length) {
//						printDebug("Using Higher initial bound based on concrete string length: " + conc_string_length);
						initialBound = conc_string_length;
					}
				}

				// create constraint from vertex data
				PrintConstraint constraint = new PrintConstraint(id, actualValue, num, timeStamp, type, value);

				// add constraint to map
				constraintMap.put(id, constraint);

				// get source constraint list and add to map
				List<Integer> sourceConstraints = (List<Integer>) obj.get("sourceConstraints");
				sourceConstraintMap.put(constraint, sourceConstraints);

				// get incoming edges
				List<Map<String, Object>> incomingEdges = (List<Map<String, Object>>) obj.get("incomingEdges");

				for (Map<String, Object> incomingEdge : incomingEdges) {
					// add edge to list
					incomingEdge.put("target", id);
					edgeData.add(incomingEdge);
				}
			} // end vertexData for loop

			// add sourceConstraints from file to their constraint in constraintMap
			// create vertices in graph
			// set sourceConstraints for each constraint
			for (PrintConstraint constraint : sourceConstraintMap.keySet()) {
				// for each constraint id
				for (int id : sourceConstraintMap.get(constraint)) {//this does nothing? no source constraints yet
					// get source constraint
					PrintConstraint sourceConstraint = constraintMap.get(id);
					// set source constraint as source for current constraints
					constraint.setSource(sourceConstraint);
				}
				// add constraint to graph
				graph.addVertex(constraint);
				graphExtra.addVertex(constraint);
				
			} // end sourceConstraint for loop

			// create edges in graph from edgeData
			for (Map<String, Object> obj : edgeData) {
				// get symbolic edge data
				int sourceId = (Integer) obj.get("source");
				PrintConstraint source = constraintMap.get(sourceId);
				int targetId = (Integer) obj.get("target");
				PrintConstraint target = constraintMap.get(targetId);
				String type = (String) obj.get("type");

				// create symbolic edge in graph from data
				SymbolicEdge edge = graph.addEdge(source, target);

				// case when edge exists already... not ideal
				if (edge == null) {
					printDebug("WARNING: edge already exists " + source + " -> " + target + " (" + type + ")");
					continue;
				}
				edge.setType(type);
				SymbolicEdge edgeExtra = graphExtra.addEdge(source, target);
				edgeExtra.setType(type);
			} // end edgeData for loop
		} catch (IOException i) {
			i.printStackTrace();
		}

		//eas for efficient processing several predicates, we
		//create a dependency map that indicate on which predicates a predicate is dependent
		//we say that two predicates are dependent if there is a symbolic node among their common ancestors
		graphExtra.orderIDsTopologically();
		graphExtra.computePredicateDependencies();
		//System.exit(1);
		return graphExtra;
	} // end loadGraph
	
	/*
	 * Run inverse solver
	 */
	private static void run_Acyclic_Inverse_r3(DirectedGraph<PrintConstraint, SymbolicEdge> graph) {
		Model_Acyclic_Inverse_Manager mFactory 					= new Model_Acyclic_Inverse_Manager(alpha, initialBound);
		Solver_Inverse<Model_Acyclic_Inverse> mSolver 		= new Solver_Inverse<Model_Acyclic_Inverse>(mFactory,	initialBound);
		Parser_2<Model_Acyclic_Inverse> mParser 				= new Parser_2<Model_Acyclic_Inverse>(mSolver, debug);
		Reporter_Inverse<Model_Acyclic_Inverse> mReporter 	= new Reporter_Inverse_BFS<Model_Acyclic_Inverse>(graph, mParser, mSolver, debug); //new Reporter_Inverse<Model_Acyclic_Inverse>(graph, mParser, mSolver, debug);
		////Reporter_Inverse<Model_Acyclic_Inverse> mReporter 	= new Reporter_Inverse<Model_Acyclic_Inverse>(graph, mParser, mSolver, debug);
		mSolver.setReduce(reduce);
		mReporter.run();
		//Decider<Model_Acyclic_Inverse> mDecider = new Decider<Model_Acyclic_Inverse>(graph, mParser);
		//mDecider.decide();
	}
	
	private static String convertSmt2ToJson(String smt2Path) throws IOException, InterruptedException {
	    // Create temp file for JSON output
	    File tempJson = File.createTempFile("smt-input-", ".json");
	    tempJson.deleteOnExit();

	    String javaPath = System.getProperty("java.home") + "/bin/java";
	    String jarPath = "lib/GenJSONs-1.0-SNAPSHOT-jar-with-dependencies.jar";

	    ProcessBuilder pb = new ProcessBuilder(
	        javaPath, "-cp", jarPath,
	        "edu.boisestate.cs.MainJSON",
	        new File(smt2Path).getAbsolutePath(),
	        tempJson.getAbsolutePath()
	    );
	    pb.redirectErrorStream(true);

	    Process process = pb.start();
	    int exitCode = process.waitFor();

	    if (exitCode != 0) {
	        throw new IOException("SMT2 conversion failed with exit code: " + exitCode);
	    }

	    return tempJson.getAbsolutePath();
	}
	
	private static void printHeader (String graph, int length, String solver, String reporter, String automata) {
		
		if (header) {
		System.out.println("Using input graph.... : " + graph);
		System.out.println("Using input length... : " + length);
		System.out.println("Using solver......... : " + solver);
		System.out.println("Using reporter....... : " + reporter);
		System.out.println("Using automata....... : " + automata + "\n");
		}
		
	}

	private static void printDebug(String s) {
		if (debug) System.out.println(s);
	}
}
