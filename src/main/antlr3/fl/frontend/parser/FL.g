grammar FL;

// antlr 3.4 tree-grammar for a SML like language.
// author: Matteo Ceruti


options {
    output=AST;
}

tokens{
APP;
TUPEL;
LIST;
SEQ;
RECORD;

}

@header {
	package fl.frontend.parser;
	
}

@lexer::header{
	package fl.frontend.parser;
}


mainexpEOF
	: mainexp EOF -> mainexp	
	;

mainexp
	:	annotateexp | fn | ifexp | caze | let | begin
	;

annotateexp
	: 
	(e=andexp->andexp)
		(COLON texp -> ^(COLON $e texp))?
	;

file :
	(fileelem)* EOF -> ^(SEQ fileelem*)
;

fileelem:
	decl|begin	
	;

replstatement
	:
	decl | seq
	;

repl :
	(replstatement->replstatement) SEMICOLON* EOF 
	
;

seq:	
	(p = exp->exp) ((SEMICOLON+ exp)+  -> ^(SEQ $p exp+))? 
;

app	:	
	(primary->primary) 
		( primary -> ^(APP $app primary)
		  |
		  DOT ID -> ^(DOT $app ID)	
		)*
	;

andexp
	:	
	(orexp->orexp) ( f=AND orexp -> ^(APP $f ^(TUPEL $andexp orexp)) )*
	;

orexp
	:	
	(eqexp->eqexp) ( f=OR relexp -> ^(APP $f ^(TUPEL $orexp eqexp)) )*
	;


eqexp
	:	
	(relexp->relexp) ( (f=EQ|f=NEQ) relexp -> ^(APP $f ^(TUPEL $eqexp relexp)) )*
	;
	
relexp:
	(compoexp->compoexp) ( (f=LT|f=LTE|f=GT|f=GTE) compoexp -> ^(APP $f ^(TUPEL $relexp compoexp)) )*
	;

compoexp
	:
	(consexp->consexp) (COMPOSE consexp -> ^(APP COMPOSE ^(TUPEL $compoexp consexp)) )*
	;

consexp
	:
	(k=addexp->addexp) (CONS consexp -> ^(APP CONS ^(TUPEL $k consexp)))?
	;
	
	

addexp
	:	
		(multexp->multexp) (addop multexp -> ^(APP addop ^(TUPEL $addexp multexp)) )*
	;

addop: ADD|MINUS|RADD|RMINUS
	;
	
multexp
	:	
		//(app->app) (multop app -> ^(APP multop ^(TUPEL $multexp app)) )*
		(prefixexp->prefixexp) (multop prefixexp -> ^(APP multop ^(TUPEL $multexp prefixexp)) )*
	;
	
multop:	MULT|DIV|RMULT|RDIV
	;

prefixexp
	:  	prefixops prefixexp -> ^(APP prefixops prefixexp) | (app->app)
	;

prefixops
	:	TILDE | RTILDE
	//| NOT
	;

exp :	mainexp
	;


literal
	:		INT | FLOAT | STRING | NIL | ID | TRUE | FALSE | CLAZZ
			| recordacc
	;

recordacc
	:	
	RECORDACC
	;


prim:	
	literal
		| '(' ')' -> ^(TUPEL)
		| '{' '}' -> ^(TUPEL)
		|'(' (p = exp->exp) (
				(',' exp)+ -> ^(TUPEL $p exp+)
				|(SEMICOLON+ exp)+ -> ^(SEQ $p exp+)
				)? ')'
		|'['(exp (',' exp)*)? ']' -> ^(LIST exp*)
		|'{' record_elem (',' record_elem)*  '}' -> ^(RECORD record_elem+)
	
;

record_elem:
(ID | STRING) EQ^ exp
;


primary :
	prim ;
	


formalprim
	:	
	literal
	| '(' ')' -> ^(TUPEL)
	|'(' (p=formals->formals) ((',' formals)+ -> ^(TUPEL $p formals+))? ')'
	|'['(formals (',' formals)*)? ']' -> ^(LIST formals*)
	;
	
formal:	
		(formalprim->formalprim)
		(
			formalprim -> ^(APP $formal formalprim)
		)*
;
	
formals
	:
	(f=formal->formal)
			(
			CONS formals -> ^(APP CONS ^(TUPEL $f formals))
			|
			COLON texp -> ^(COLON $f texp)
			)?
	;

fn 	:	FN formals EQARROW exp
		((PIPE)=>PIPE formals EQARROW exp)* -> ^(FN (^(EQARROW formals exp))+)
		
		
;



fun 	:	FUN id=ID funPart
		((PIPE)=>PIPE ID funPart)* -> ^(REC $id  ^(FUN funPart+) )
		
	;
funFormal
	:	 
		(p=formalprim->formalprim)
			(
			formalprim+ -> ^(TUPEL $p formalprim+)
			)? 
			
		;
	
funPart 	:	funFormal EQ exp
		 -> ^(EQARROW funFormal exp) 
		
	;




casePart
	:	(formals EQARROW exp) -> ^(EQARROW formals exp)
	;
caze:	
		CASE primary OF casePart ((PIPE)=>PIPE casePart)*
		
		-> ^(APP ^(FN casePart+) primary)
	;

let :
		LET decl+ IN e=seq END -> ^(LET ^(LIST decl+) $e)
	;
begin:
	BEGIN e=seq END -> seq
	;

decl: val | typedecl | datatypedecl | fun
;

val	:	valRec formals EQ exp -> ^(valRec formals exp)
;

valRec	:	(VAL->VAL) (REC->REC)? ;

ifexp	: IF exp THEN exp ELSE exp -> ^(IF exp exp exp)	;

	
datatypedecl
	:	DATATYPE ID EQ
		typeconst
		(PIPE typeconst)*
		
		-> ^(DATATYPE ID typeconst+)
	;	

typeconst
	:	
		(ID->ID) (OF texp -> ^(OF $typeconst texp))?
	;	

typedecl
	:	TYPE ID EQ texp -> ^(TYPE ID texp)
	;

texp:	(tmultexp->tmultexp) ( TARROW tmultexp -> ^(TARROW $texp tmultexp) )*
;

tmultexp:	
	(x=tprim->tprim) 
		((MULT tprim )+ -> ^(MULT $x tprim+))?
;

tprim
	:
		TID
		| ID
		| '(' ')' -> ^(TUPEL)
		| '{' '}' -> ^(TUPEL)
		| '(' texp ')' -> texp
		| '{' trecord_elem (',' trecord_elem)*  '}' -> ^(RECORD trecord_elem+)
	;
trecord_elem:
	ID COLON^ texp
	;

DATATYPE:	'datatype' ;
TYPE:	'type' ;
TID	:	'\'' ('a'..'z'|'A'..'Z')+ ('0'..'9')*;
TARROW:	'->';




//

fragment COLON
	:	 ':' ;
fragment CONS
	:	'::';

COLON_OR_CONS:	(CONS)=> CONS {$type=CONS;}
		| COLON {$type=COLON;} ;
		
		
DOT	:	'.';		
SEMICOLON:	';';
PIPE:	'|';
CASE:	'case';
OF	:	'of';
TRUE:	'true';
FALSE:	'false';
IF :	'if';
THEN:	'then';
ELSE:	'else';
NIL :	'nil';
COMPOSE:	'o';
EQARROW 	:	'=>';
OR	:	'OR';
AND	:	'AND';
EQ 	:	'=';
NEQ 	:	'<>';
FN	:	'fn';
FUN	: 	'fun';
LET	:	'let';
IN	:	'in';
BEGIN	:	'begin';
END	:	'end';
VAL	:	'val';
REC	:	'rec';
RADD:	'+.';
RMINUS:	'-.';
RMULT :	'*.' ;
RDIV:	'/.';
LT: '<';
LTE: '<=';
GT: '>';
GTE: '>=';
ADD	:	'+';
MINUS:	'-';
MULT:	'*';
DIV	:	'/';
TILDE:	'~';
RTILDE:	'~.';
//RECORDACC:	'#' (ID|INT);
//NOT	:	'!';
//RECORDACC:	'#' ;
RECORDACC
	:	
	('#' (
	//STRING_FRAG  // too dynamic!?
	//| 
	ID
	| 
	INT
	))
	{setText($text.substring(1, $text.length()));}
	
	;

	

//ID  :	('a'..'z'|'A'..'Z'|'_'|'+'|'-'|'*'|'/'|'.') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'+'|'-'|'*'|'/'|'.')*
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

CLAZZ
	:	'class' WS+ ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.')*
		{setText($text.replaceAll("\\s",""));
		setText($text.substring("class".length(), $text.length()));
		}
	;



INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    //|   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    |   '(*' ( options {greedy=false;} : . )* '*)' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

fragment
STRING_FRAG
    :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
    
    {setText($text.substring(1, $text.length()-1));}
    
    ;
    

STRING
    :  STRING_FRAG
    ;
    
        


CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
