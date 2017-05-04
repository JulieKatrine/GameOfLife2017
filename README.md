# Game-of-Life-2017
Assignment in Program Development - DATS1600
Created by Julie Katrine Høvik and Niklas Johansen

Completed assignments:
•	Assignment 1 – The first obligatory task was completed and handed in the 02.02.17 in Fronter.

•	Assignment 2 – we created a static board (see GameBoardStatic) that was later replaced with a dynamic board (see GameBoardDynamic).

•	Assignment 3 – implemented AnimationTimer for animation. We later discovered that this class was slowing the simulation down
	– and we replaced it with our own timer class (see UpdateTimer).

•	Assignment 4 – implemented Simulator class with support for dynamic rules with the SimulationRule Interface.
    Created Javadoc and UnitTests.
	o	Extension: We optimized the board in a different way than suggested in the assignment, see JavaDoc for GameBoardDynamic.
	o	Extension: Implemented dynamic rules for the user to select or create.

•	Assignment 5 (Obligatory task 2) – file import implemented for .rle, .lif and .life. See model.patternIO package.
	Handles all exceptions with try and catch, and appropriate alert boxes for the user, see PatternFormatException.
	o	Extension: Support for loading and exporting metadata.
	o	Extension: Camera movement. Drag-and-move is implemented, see menu-bar in application Help>Getting Started.

•	Assignment 6 – dynamic board implemented see GameBoardDynamic.
	Similar to the “Valgfri Ekstraoppgave” we implemented our dynamic board class in a similar way as the ArrayList works internally,
	which increased the speed remarkably and decreased the memory usage.
	To make sure we fulfilled the requirements of the assignment we also created the class GameBoardDynamicList.

•	Assignment 7 – Implemented threads.
	o	Extension: Implemented threaded simulation of the board using a fixed thread pool.
		We chose to avoid the common lock-based synchronization, see Javadoc for ThreadedSimulationImpl for a deeper explanation.


Additional extensions and other features:
•	“Manipulering og GIF”: Pattern editor with support for exporting .rle and .gif.
	The editor recognizes repeating patterns and suggests GIF-export from the generation-strip.
	See the PatternEditorController and the AnimationExportCotroller classes.

•	Pattern Chooser – We created our own pattern chooser dialog (Click: File>Open Pattern, in the application),
	where the user can read the metadata and preview the loaded patterns. Some preloaded patterns come with the game.
	Files can be loaded through FileChooser, URL or drag-and-drop.

•	We have a consistent layout-theme throughout the whole application, implemented with CSS.
    Making the application responsive has also been a priority for us.
	I.e. Threaded pattern loading and scalable GUI.


Information:
•	UnitTests are localized in GameOfLife2017>unitTests
