package es.ehu.si.ixa.pipe.nerc.lexer;

/* --------------------------Usercode Section------------------------ */


import java.io.Reader;
import java.util.logging.Logger;

import es.ehu.si.ixa.pipe.nerc.Name;
import es.ehu.si.ixa.pipe.nerc.NameFactory;

	
/* -----------------Options and Declarations Section----------------- */

%%

%class NumericLexer
%unicode
%type Name
%char
%caseless

/* 
 * Member variables and functions
 */

%{

  private NameFactory nameFactory;
  private static final Logger LOGGER = Logger.getLogger(NumericLexer.class.getName());
  private boolean seenUntokenizableCharacter;
  private enum UntokenizableOptions { NONE_DELETE, FIRST_DELETE, ALL_DELETE, NONE_KEEP, FIRST_KEEP, ALL_KEEP }
  private UntokenizableOptions untokenizable = UntokenizableOptions.FIRST_DELETE;
  
  
  /////////////////
  //// OPTIONS ////
  /////////////////
  
  
  public NumericLexer(Reader breader, NameFactory aNameFactory) {
    this(breader);
    this.nameFactory = aNameFactory;
  }

  
  ////////////////////////
  //// MAIN FUNCTIONS ////
  ////////////////////////
  
  
  private Name makeName(String nameString, String neType) {
    Name name = nameFactory.createName(nameString, neType, yychar, yylength());
    return name;
  }
  
  private Name makeName() {
    return makeName("MISC","MISC");
  }

%}

  ////////////////
  //// MACROS ////
  ///////////////

/*---- SPACES ----*/

SPACE = [ \t\u0020\u00A0\u2000-\u200A\u3000]

/*---- GENERIC NUMBERS ----*/

DIGIT = [:digit:]|[\u07C0-\u07C9]
NUM = {DIGIT}+|{DIGIT}*([.:,\u00AD\u066B\u066C]{DIGIT}+)+
NUMBER = [\-+]?{NUM}[.,]?
FRACTION = ({DIGIT}{1,4}[- \u00A0])?{DIGIT}{1,4}(\\?\/|\u2044){DIGIT}{1,4}
FRACTION_TB3 = ({DIGIT}{1,4}-)?{DIGIT}{1,4}(\\?\/|\u2044){DIGIT}{1,4}
OTHER_FRACTION = [\u00BC\u00BD\u00BE\u2153-\u215E]


//////////////
//// DATE ////
//////////////

/*---- NUMERIC DATE ----*/

// 12/-04/-2013
DATE_STANDARD = {DIGIT}{1,2}[\-\/]{DIGIT}{1,2}[\-\/]{DIGIT}{2,4}
// 2013-/04/-12
DATE_BASQUE = {DIGIT}{2,4}[\-\/]{DIGIT}{1,2}[\-\/]{DIGIT}{1,2}
//DATE2 = ([1-9]|[0-3][0-9])\\?\/([1-9]|[0-3][0-9])\\?\/[1-3][0-9]{3}
//DATE3 = [12][0-9]{3}[-/](0?[1-9]|1[0-2])[-/][0-3][0-9]
YEAR = [1-3][0-9]{3}|[0-9]{2}
APOSYEAR = {SPACE}'[0-9]{2}s?{SPACE}
DAY = ([1-9]|[12][0-9]|3[01])(st|nd|rd|th|\.)?

//NUMERIC_DATE = {DATE_STANDARD}|{DATE_BASQUE}|{DATE2}|{DATE3}|{YEAR}
NUMERIC_DATE = {DATE_STANDARD}|{DATE_BASQUE}|{APOSYEAR}

/*---- WORD DATE ----*/

MONTH_DE = Januar|Jänner|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember
MONTH_EN = January|February|March|April|June|July|August|September|October|November|December
MONTH_ES = Enero|Febrero|Marzo|Abril|Mayo|Junio|Julio|Agosto|Septiembre|Octubre|Noviembre|Diciembre
MONTH_FR = Janvier|Février|Mars|Avril|Mai|Juin|Juillet|Août|Septembre|Octobre|Novembre|Décembre
MONTH_IT = Gennaio|Febbraio|Marzo|Aprile|Giugno|Luglio|Agosto|Settembre|Ottobre|Novembre|Dicembre
MONTH_NL = Januari|februari|maart|april|juni|juli|augustus|september|oktober|november|december

MONTH = {MONTH_DE}|{MONTH_EN}|{MONTH_ES}|{MONTH_FR}|{MONTH_IT}|{MONTH_NL}

DAY_DE = Montag|Dienstag|Mittwoch|Donnerstag|Freitag|Samstag|Sonntag
DAY_EN = Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday
DAY_ES = Lunes|Martes|Miércoles|Jueves|Viernes|Sábado|Domingo
DAY_FR = Lundi|Mardi|Mercredi|Jeudi|Vendredi|Samedi|Dimanche
DAY_IT = Lunedi|Martedì|Mercoledì|Giovedi|Venerdì|Sabato|Domenica
DAY_NL = Maandag|dinsdag|woensdag|donderdag|vrijdag|zaterdag|zondagh

DAY_SYMBOL = {DAY_DE}|{DAY_EN}|{DAY_ES}|{DAY_FR}|{DAY_IT}|{DAY_NL}

WORD_DATE = {MONTH}|{DAY_SYMBOL}

/*---- ABBREV_DATE ----*/

ABBREV_MONTH_DE = (Jän|März|Mai|Okt|Dez)\.?{SPACE}
ABBREV_MONTH_EN = (Jan|Feb|Mar|Apr|Jun|Jul|Aug|Sep|Sept|Oct|Nov|Dec)\.?{SPACE}
ABBREV_MONTH_ES = (Ene|Febr|Abr|Ag|Dic)\.?{SPACE}
ABBREV_MONTH_FR = (janv|févr|mars|avril|juin|juil|août|déc)\.?{SPACE}
ABBREV_MONTH_IT = (genn|febbr|magg|giugno|luglio|sett|ott)\.?{SPACE}
ABBREV_MONTH_NL = (maart|mei|juni|juli|okt)\.?{SPACE}

ABBREV_MONTH = {ABBREV_MONTH_DE}|{ABBREV_MONTH_EN}|{ABBREV_MONTH_ES}|{ABBREV_MONTH_FR}|{ABBREV_MONTH_IT}|{ABBREV_MONTH_NL}

ABBREV_DAY_DE = (So|Mo|Di|Mi|Do|Fr|Sa)\.{SPACE}
ABBREV_DAY_EN = (Mon|Tue|Tues|Wed|Thu|Thurs|Fri|Sat|Sun)\.{SPACE}
ABBREV_DAY_ES = (Lun|Mar|Miér|Jue|Vier|Sáb|Dom)\.{SPACE}
ABBREV_DAY_FR = (lun|mer|jeu|ven|sam|dim)\.{SPACE}
ABBREV_DAY_IT = (mar|gio|ven|sab)\.{SPACE}
ABBREV_DAY_NL = (ma|woe|vrij|za|zo|wo|vr)\.{SPACE}

ABBREV_DAY = {ABBREV_DAY_DE}|{ABBREV_DAY_EN}|{ABBREV_DAY_ES}|{ABBREV_DAY_FR}|{ABBREV_DAY_IT}|{ABBREV_DAY_NL}

ABBREV_DATE = {ABBREV_MONTH}|{ABBREV_DAY}

/*---- DATE EXPRESSIONS ----*/

MONTHS = {MONTH}|{ABBREV_MONTH}
DAY_MONTH_YEAR = {DAY}{SPACE}+(of|de)?{SPACE}*({MONTHS}|May){SPACE}+(of|de)?{SPACE}*{YEAR}?
MONTH_DAY_YEAR = ({MONTHS}|May){SPACE}+{DAY}{SPACE}*{YEAR}?
YEAR_MONTH_DAY = {YEAR}{SPACE}+({MONTHS}|May){SPACE}+(the)?{DAY}

SIMPLE_DATE = {NUMERIC_DATE}|{WORD_DATE}|{ABBREV_DATE}
DATE_YEAR = {SIMPLE_DATE}{SPACE}+,?{SPACE}+{YEAR}

/*---- DATE ----*/

DATE = {DAY_MONTH_YEAR}|{MONTH_DAY_YEAR}|{YEAR_MONTH_DAY}|{SIMPLE_DATE}|{DATE_YEAR}

//////////////
//// TIME ////
//////////////

/*---- GENERIC TIME WORDS ----*/

TIME_WORDS_DE = Morgen|Abend|Nacht|Uhr|Mitternacht|Mittags|Abendessen|Abendbrot|Nachmittag|Mittag|Dämmerung|Sonnenaufgang|Sonnenuntergang|Tagesanbruch|Tag
TIME_WORDS_EN = (morning|evening|night|noon|midnight|teatime|lunchtime|dinnertime|suppertime|afternoon|midday|dusk|dawn|sunup|sundown|daybreak|day)
TIME_WORDS_ES = mañana|tarde|noche|mediodía|medianoche|hora{SPACE}del{SPACE}té|hora{SPACE}del{SPACE}café|cena|suppertime|atardecer|alba|amanecer|ocaso
TIME_WORDS_FR = matin|soirée|nuit|midi|minuit|heure{SPACE}du{SPACE}thé|midi|dîner|souper|après-midi|midi|crépuscule|aube|lever{SPACE}du{SPACE}soleil|coucher{SPACE}du{SPACE}soleil|lever{SPACE}du{SPACE}jour|jour
TIME_WORDS_IT = mattina|sera|notte|mezzogiorno|mezzanotte|teatime|pranzo|cena|suppertime|pomeriggio|mezzogiorno|alba|giorno
TIME_WORDS_NL = ochtend|avond|nacht|middag|midnight|teatime|lunch|etenstijd|suppertime|middag|schemering|zonsopgang|zonsondergang|dageraad|dag

TIME_WORDS = {TIME_WORDS_DE}|{TIME_WORDS_EN}|{TIME_WORDS_ES}|{TIME_WORDS_FR}|{TIME_WORDS_IT}|{TIME_WORDS_NL}

/*---- TIME WORDS EXPRESSIONS ----*/

TIME_WORDS_EXPRESSIONS_EN = (over{SPACE}the){SPACE}{TIME_WORDS_EN}
TIME_WORDS_EXPRESSIONS_ES = (a{SPACE}lo{SPACE}largo{SPACE}de{SPACE}|durante|en{SPACE}una){SPACE}{TIME_WORDS_ES}

TIME_WORDS_EXPRESSIONS = {TIME_WORDS_EXPRESSIONS_EN}|{TIME_WORDS_EXPRESSIONS_ES}

/*---- NUMERIC TIME ----*/

TWELVE_TIME = [0-2]?[0-9]:[0-5][0-9]
TWENTYFOUR_TIME = [0-2][0-9]:[0-5][0-9]:[0-5][0-9]
ARMY_TIME = 0([0-9])([0-9]){2}
AMPM = (a\.?m\.?)|(p\.?m\.?)

/*---- TIME EXPRESSIONS ----*/
TIME_AMPM = {TWELVE_TIME}{SPACE}*{AMPM}?
TIMEAMPM = {NUMBER}{SPACE}*{AMPM}
TIME = {TIME_WORDS_EXPRESSIONS}|{TIME_AMPM}|{TIMEAMPM}|{TWENTYFOUR_TIME}|{ARMY_TIME}|{TIME_WORDS}

///////////////
//// MONEY ////
///////////////

CURRENCY_WORD = (dollar|cent|euro|pound)s?|penny|pence|yen|yuan|won|USD
CURRENCY_SYMBOL = \$|&#163|#|US\$|HK\$|A\$|\u00A2|\u00A3|\u00A4|\u00A5|\u0080|\u20A0|\u20AC|\u060B|\u0E3F|\u20A4|\uFFE0|\uFFE1|\uFFE5|\uFFE6

SYMBOL_MONEY = {CURRENCY_SYMBOL}{SPACE}*{NUMBER}
MONEY_SYMBOL = {NUMBER}{SPACE}*({CURRENCY_SYMBOL}|{CURRENCY_WORD})

/*---- MONEY ----*/

MONEY = {SYMBOL_MONEY}|{MONEY_SYMBOL}

/////////////////
//// PERCENT ////
/////////////////

NUMBER_PERCENT = {NUMBER}{SPACE}*(prozent|percent|por{SPACE}ciento|pour{SPACE}cent|per{SPACE}cento|procent|%)
PERCENT_NUMBER = %{SPACE}*{NUMBER}
PERCENT_ABBREV = {NUMBER}{SPACE}*pct\.?

/*---- PERCENT ----*/
PERCENT = {NUMBER_PERCENT}|{PERCENT_NUMBER}|{PERCENT_ABBREV}

/* ------------------------Lexical Rules Section---------------------- */

%%


/*---- DATES ----*/


{DATE}                      { String txt = yytext(); return makeName(txt, "DATE"); }

/*---- TIME ----*/

{TIME}                      { String txt = yytext(); return makeName(txt, "TIME"); }
/*---- MONEY ----*/

{MONEY}                     { String txt = yytext(); return makeName(txt, "MONEY"); }

/*---- PERCENT ----*/       

{PERCENT}                   { String txt = yytext(); return makeName(txt, "PERCENT"); } 
                            
/*---- skip non printable characters ----*/

[\\x00-\\x19]		{ }

/*---- warn about other non tokenized characters ----*/

.       { String str = yytext();
          int first = str.charAt(0);
          String msg = String.format("Untokenizable: %s (U+%s, decimal: %s)", yytext(), Integer.toHexString(first).toUpperCase(), Integer.toString(first));
          switch (untokenizable) {
            case NONE_DELETE:
              break;
            case FIRST_DELETE:
              if ( ! this.seenUntokenizableCharacter) {
                //LOGGER.warning(msg);
                this.seenUntokenizableCharacter = true;
              }
              break;
            case ALL_DELETE:
              //LOGGER.warning(msg);
              this.seenUntokenizableCharacter = true;
              break;
            case NONE_KEEP:
              return makeName();
            case FIRST_KEEP:
              if ( ! this.seenUntokenizableCharacter) {
                //LOGGER.warning(msg);
                this.seenUntokenizableCharacter = true;
              }
              return makeName();
            case ALL_KEEP:
              //LOGGER.warning(msg);
              this.seenUntokenizableCharacter = true;
              return makeName();
          }
        }
<<EOF>> 					{ return null; }

/*skip everything else*/
/*.|\n 			{ } */


