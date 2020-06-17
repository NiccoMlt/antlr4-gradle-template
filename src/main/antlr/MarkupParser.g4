parser grammar MarkupParser;

options { tokenVocab=MarkupLexer; }

file        : element* ;

attribute   : ID EQUALS STRING ;

content     : TEXT ;

element     : (content | tag) ;

tag         : OPEN ID attribute? CLOSE element* OPEN SLASH ID CLOSE ;
