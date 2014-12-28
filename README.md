# IVIS
Interactive visualization of information network 

## Abstract
This thesis is focused on solution and enhancement of interaction of information networks visualization. An information network is most frequently represented in form of a graph, a mathematical graph, 
which is increasingly used as a natural / basic form of data. Information networks visualization
 has wide range of use which can be found in f.e. social networks, Wikipedia, e-mail and internet in general. However, currently existing visualization solutions are problematic. They only display little volume of data in an insufficiently organized manner. In this thesis, we will strive for the best solution, We will try to perfect it and come up with another new ideas. We describe particular interactive Instruments, display elements, filtration of vertices and edges. As well as that, we will focus on existing format of files and briefly comprise physical representation of data. In each section we aim to state positive and negative aspects of every potential solution and the conclusion will include description of the solution which we consider to be the best, supported by explanation of my choice. My work is supposed to result in a functional application, which will reflect and explain the relation between selected vertices of the graph. The application should be interactive enough, display the data defined in file. User should avoid loading of overly complex graphs. 

## Short description
Actually we are focused on sparse graphs, generated from interested corpus documents (Enron Graph Corpus, Gorila).  Where we are trying find relations between entities in interactive environment.

## Requirements for visualization
- big data graphs, millions of vertices, tens millions of edges
- savings graph out of main memory
- fast loading and processing

## Used technologies
- non-blocking IO operations, memory-mapped files
- quad-tree structure, soft references, ...
- based on JUNG library - so support various JUNG layouts, formats and new gSemSearch format

## Custom storage of data
I prepared custom system to store graphs data.
Graphs are preprocessed, stored in key-value pair, only keys are stored at main memory. We really matters about savings every byte. Details are described in document.

## Credits
Supervisor [RNDr. Michal Laclavík, PhD.](https://github.com/misos)