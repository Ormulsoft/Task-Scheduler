# Group 15 - Task Scheduling Project

## Brief

The purpose of this project was to create a system that schedules a set of tasks onto a number of processors.
The input is in the form of a \*.dot file, which contains a graph of all the tasks and their dependencies. 
The system is required to output an *OPTIMAL* schedule ( earliest finishing time) , while retaining all the dependencies of the original graph.

## Design

The design of this system uses dedicated IO classes, and OOP heirarchial structures for the different graphing datastructures and algorithm components. More detail is listed in the Wiki.

## Algorithm
The currently implemented algorithm is the standard A/* algorithm. The current cost function uses computational bottom level, and
lastest finishing time.

# Installing
To install this project, please download a runnable JAR File submitted.
CLI input is java -jar <filename.jar> \[input_filename\] \[num_processors\] <options...>

## Options
Options are 
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
