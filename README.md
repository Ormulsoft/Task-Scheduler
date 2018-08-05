# Group 15 - Task Scheduling Project

## Brief

The purpose of this project is to create a system that schedules a set of tasks onto an abitrary number of processors.
The input is in the form of a .DOT file, which contains a directed acyclic graph of all the tasks and their dependencies. 
The system is required to output an **optimal** schedule, that is, a valid schedule with the earliest finishing time, while retaining all the dependencies of the original graph.

## Design

The design of this system uses dedicated IO classes, and object-oriented hierarchical structures for the various graphing and algorithm components. This design is exlpored in detail within the Wiki.

## Algorithm
The currently implemented algorithm is the standard A* algorithm. The cost function currently uses computational bottom level, and
lastest finishing time.

# Running
To install this project, please download the runnable JAR File submitted.
CLI input is java -jar <filename.jar> \[input_filename\] \[num_processors\] <options...>

## Options
The following options are supported by the system: 
- \-p <numberCores>       The number of processor cores to use
- \-o <output_filename>   The file to output to.
- \-v                     Flag to visualise or not.


# Group Members
| Github Username | Name | UPI | Uni ID |
| --------------- | ---- | --- | ------ |
| Eugene-Bulog | Eugene Bulog | ebul920 | 985903606 |
| ShaneBarboza | Shane Barboza | sbar539 | 536431628 |
| mfrost33 | Matthew Frost | mfro529 | 882530485 |
| hpt09 | Harpreet Singh | hsin612 | 622623765 |
| Nikhil-Dreddy | Nikhil Donthireddy | ndon616 | 497601419 |
