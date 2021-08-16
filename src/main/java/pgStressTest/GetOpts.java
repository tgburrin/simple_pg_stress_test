package pgStressTest;

import java.util.Hashtable;

public class GetOpts {
    /***************************************************************************
    * Method:      getopts
    *
    * Arguments:   the string of options to look for and command line arguments
    *
    * Returns:     nothing
    *
    * Description: Looks for command line arguments and sets their values in
    *              the global Hashtable Options
    ***************************************************************************/
    public static Hashtable<String,String> getopts (String optstr, String[] args) {
        Hashtable<String, String> Options = new Hashtable<String, String>();

        int argn = 0;
        int optn = 0;

        Hashtable<Character, Boolean> optArgs = new Hashtable<Character, Boolean>();
        Hashtable<String,String> result = new Hashtable<String, String>();

        char currentOpt = '\0';
        for(char c : optstr.toCharArray()) {
    		if (Character.isSpaceChar(c))
        		continue;

        	if ( Character.isLetter(c) ) {
    			currentOpt = c;
    			optArgs.put(Character.valueOf(c), Boolean.valueOf(false));
        	} else if (c == ':' && Character.isLetter(currentOpt)) {
				optArgs.replace(Character.valueOf(currentOpt), Boolean.valueOf(true));
	        }
        }

        for(int i=0; i<args.length; i++) {
        	String arg = args[i];
        	if ( arg.matches("^-[a-zA-Z]{1}$") ) {
	        	if ( optArgs.containsKey(Character.valueOf(arg.charAt(1))) ) {
	        		if ( optArgs.get(Character.valueOf(arg.charAt(1)))) {
	        			i++;
	        			if ( i < args.length )
	        				result.put("opt_"+Character.valueOf(arg.charAt(1)), args[i]);
	        		} else {
	        			result.put("opt_"+Character.valueOf(arg.charAt(1)), new String());
	        		}
	        	}
        	}
        }

        return result;
    }
}