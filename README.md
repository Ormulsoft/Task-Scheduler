
<div align="center">
<a><img style="display:inline-block;" src="./ormulsoft.png" ></a>
<br>
</div>


# Group 15 - Task Scheduling Project

This an implementation of the task scheduling system for SOFTENG306 Assignment 1.

[Javadocs here](https://eugene-bulog.github.io/Softeng-306-Group-15/overview-summary.html)

## Brief

The purpose of this project is to create a system that schedules a set of tasks onto an abitrary number of processors.
The input is in the form of a .DOT file, which contains a directed acyclic graph of all the tasks and their dependencies. 
The system is required to output an **optimal** schedule, that is, a valid schedule with the earliest finishing time, while retaining all the dependencies of the original graph.

## Design

The design of this system uses dedicated IO classes, and object-oriented hierarchical structures for the various graphing and algorithm components. This design is exlpored in detail within the Wiki.

## Algorithm
The currently implemented branch-and-bound algorithm uses a cost function calculated by computational bottom level, idle time and
lastest finishing time when finding an optimal schedule.

## Running
To install this project, please download the runnable JAR File submitted.<br/> Enter the following on the command line: `java -jar <filename.jar> <input_graph.DOT> <num_processors> <options...>`

NOTE: On Canvas, the submitted .jar file is named `Scheduler-2.jar`.

## Building from source
To build from source, run a maven update command on the project, then run the io.main class (make sure the command-line arguments from the Running/Options section are specified in the run configuration)

## Options
The following options are supported by the system: 
- `-p <numberCores>`       The number of processor cores to use
- `-o <output_filename>`   The file to output to.
- `-v`                     Flag to visualise or not.


