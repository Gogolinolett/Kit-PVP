package io.github.Gogolinolett.WW1Job.SimpleDBMTadapter;

import io.github.SebastianDanielFrenz.SimpleDBMT.error.InterpreterIDMissingException;
import io.github.SebastianDanielFrenz.SimpleDBMT.expandable.ValueManager;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBString;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBVersion;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBboolean;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBbyte;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBdouble;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBfloat;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBint;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBlong;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBshort;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.DBvalue;
import io.github.SebastianDanielFrenz.SimpleDBMT.varTypes.Saveable;
import io.github.Gogolinolett.DungeonPlugin.SimpleDBMTadapter.DBLocation;
import io.github.SebastianDanielFrenz.SimpleDBMT.CrashedDBsep;

/**
 * 
 * @since SimpleDB 1.1.0
 * @version SimpleDB 2.0.0
 */

public class CustomValueManager extends ValueManager {

	private char IDsep = CrashedDBsep.sepValueID;

	@Override
	public DBvalue Interpret(String text) {
		DBvalue output;
		String[] parts = text.split("\\\\" + IDsep);
		char ID = parts[0].toCharArray()[0];
		String value = parts[1].replace((char) 0, '\\');

		if (ID == CrashedDBsep.ID_DBint) {
			output = new DBint();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBfloat) {
			output = new DBfloat();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBdouble) {
			output = new DBdouble();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBString) {
			output = new DBString();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBboolean) {
			output = new DBboolean();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBbyte) {
			output = new DBbyte();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBlong) {
			output = new DBlong();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBshort) {
			output = new DBshort();
			output.Parse(value);
		} else if (ID == CrashedDBsep.ID_DBVersion) {
			output = new DBVersion();
			output.Parse(value);
		} else if (ID == 11) {
			output = new DBLocation();
			output.Parse(value);
		} else {
			output = new DBString("lol");
		}
		return output;
	}

	public char GetID(Saveable value) throws InterpreterIDMissingException {
		if (value instanceof DBint) {
			return CrashedDBsep.ID_DBint;
		} else if (value instanceof DBfloat) {
			return CrashedDBsep.ID_DBfloat;
		} else if (value instanceof DBdouble) {
			return CrashedDBsep.ID_DBdouble;
		} else if (value instanceof DBString) {
			return CrashedDBsep.ID_DBString;
		} else if (value instanceof DBboolean) {
			return CrashedDBsep.ID_DBboolean;
		} else if (value instanceof DBbyte) {
			return CrashedDBsep.ID_DBbyte;
		} else if (value instanceof DBlong) {
			return CrashedDBsep.ID_DBlong;
		} else if (value instanceof DBshort) {
			return CrashedDBsep.ID_DBshort;
		} else if (value instanceof DBVersion) {
			return CrashedDBsep.ID_DBVersion;
		} else if (value instanceof DBLocation) {
			return 11;

		} else {
			throw new InterpreterIDMissingException();
		}
	}

	public char getIDsep() {
		return IDsep;
	}

	public void setIDsep(char iDsep) {
		IDsep = iDsep;
	}

	@Override
	public String Save(DBvalue value) {
		String output = "";
		output += GetID(value);
		output += "\\" + IDsep;
		output += value.Save().replace("\\", "\\\\");
		return output;
	}

}
