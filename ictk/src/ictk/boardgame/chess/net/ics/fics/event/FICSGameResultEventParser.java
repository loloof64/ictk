/*
 *  ICTK - Internet Chess ToolKit
 *  More information is available at http://ictk.sourceforge.net
 *  Copyright (C) 2002 J. Varsoke <jvarsoke@ghostmanonfirst.com>
 *  All rights reserved.
 *
 *  $Id$
 *
 *  This file is part of ICTK.
 *
 *  ICTK is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  ICTK is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ICTK; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ictk.boardgame.chess.net.ics.fics.event;
import ictk.boardgame.chess.net.ics.event.*;
import ictk.boardgame.chess.net.ics.*;
import ictk.util.Log;

import java.util.regex.*;
import java.io.IOException;


public class FICSGameResultEventParser extends ICSEventParser {
   //static/////////////////////////////////////////////////////////////////
   public static final Pattern masterPattern;

   static {
      masterPattern  = Pattern.compile(
         "^:?("
//<template function=regex>
	 + "\\{Game\\s"
	 + "(\\d+)"               //game number
	 + "\\s\\("
	 + REGEX_handle           //white
	 + "\\svs\\.\\s"
	 + REGEX_handle           //black
	 + "\\)\\s"
	 + "([^}]+)"              //description
	 + "\\}\\s"
	 + "(\\S+)"               //results
//</template>
	 + ")"                    //end match
         , Pattern.MULTILINE);
   }

   //instance///////////////////////////////////////////////////////////////
   public FICSGameResultEventParser () {
      super(masterPattern);
   }

   /* createICSEvent ********************************************************/
   public ICSEvent createICSEvent (Matcher match) {
      ICSEvent evt = new ICSGameResultEvent();
         assignMatches(match, evt);

	 return evt;
   }

   /* assignMatches *********************************************************/
   public void assignMatches (Matcher m, ICSEvent event) {
      ICSGameResultEvent evt = (ICSGameResultEvent) event;

      evt.setFake(detectFake(m.group(0)));
//<template function=assignMatches>

      evt.setWhitePlayer(m.group(3));
      evt.setBlackPlayer(m.group(4));

      evt.setMessage(m.group(5));
      evt.setResult(new ICSResult(m.group(6)));

      //numbers
      try {
         evt.setBoardNumber(Integer.parseInt(m.group(2)));
      }
      catch (NumberFormatException e) {
         Log.error(Log.PROG_WARNING,
           "Can't parse board number for: " 
	   + m.group(2) + " of " + m.group(0));
         evt.setEventType(ICSEvent.UNKNOWN_EVENT);
         evt.setMessage(m.group(0));
      }
//</template>
   }

   /* toNative ***************************************************************/
   public String toNative (ICSEvent event) {

      if (event.getEventType() == ICSEvent.UNKNOWN_EVENT)
         return event.getMessage();

      ICSGameResultEvent evt = (ICSGameResultEvent) event;
      StringBuffer sb = new StringBuffer(20);

//<template function=toNative>
      sb.append("{Game ")
        .append(evt.getBoardNumber())
	.append(" (")
	.append(evt.getWhitePlayer())
	.append(" vs. ")
	.append(evt.getBlackPlayer())
	.append(") ")
	.append(evt.getMessage())
	.append("} ")
	.append(evt.getResult());
//</template>

      return sb.toString();
   }
}


