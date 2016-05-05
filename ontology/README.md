#Code ontology

Basic ontology describing programming languages. The dataset focuses on highlight atomic entities in programs, such as functions and data types, providinig nevertheless definitions for widely used entities.
In order to achieve this, a general class is provided, **Datum**, defining a generic and atomic code element. Since most of
languages share a common base, most abstract predicates are defined:
  
- **namespace:** defining a closure on a name table
- **name:** defining an identifier for an entity
- **type:** defining the type of a given element
- **requests:** defining an abstract link between two entities and stating that in order to be able
to properly define an entity, another entity is required

A less abstract set of predicates then extends the very core of the ontology:

- **has_parameter / parameter position:** defining a link between a functional abstraction and its parameter(s) and its/their position(s).
- **returns:** defining a link between an entity and the type it evaluates to. Note that such a link addresses both the type of a standard
 entity and the type of an expression
- **value:** defining a link between an entity and its default value, if provided 

##Object-Oriented

Object oriented paradigm is defined through its most characteristic features:

- **prototype**
- **class**
- **interface**
- **object**
- **method**
- **field**

These four entities help keep the core dataset concise and highly flexible without losing much expressiveness.
Most of the predicates defined are able to define coherent triples between more than just one type of entity, thus
providing one more level of abstraction:

- **extends:** defining the extension of a *class/interface*
- **implements:** defining the link between an *interface* and its implementing *class* 
- **encapsulation:** defining a namespace closure in user-defined type system
- **declared_by:** defining a link between a *method/field* and the type declaring it

At last the ontology provides definitions for the [Java programming language](https://www.java.com/):

- **package:** defining a link between a *class/interface* and its package
- **has_modifier:** defining a link between a *class/interface/field* and its visibility modifier