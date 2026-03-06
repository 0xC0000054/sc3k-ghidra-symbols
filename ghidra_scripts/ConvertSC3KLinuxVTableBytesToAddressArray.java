//Convert SimCity 3000 Unlimited Linux VTables from an array of bytes to an arreay of pointers
//@author 0xC0000054
//@category Symbol
//@keybinding 
//@menupath 
//@toolbar 
//@runtime Java

import java.util.Collection;
import ghidra.app.script.GhidraScript;
import ghidra.program.model.sourcemap.*;
import ghidra.program.model.lang.protorules.*;
import ghidra.program.model.mem.*;
import ghidra.program.model.lang.*;
import ghidra.program.model.pcode.*;
import ghidra.program.model.data.ISF.*;
import ghidra.program.model.util.*;
import ghidra.util.data.DataTypeParser;
import ghidra.util.data.DataTypeParser.AllowedDataTypes;
import ghidra.program.model.reloc.*;
import ghidra.program.model.data.*;
import ghidra.program.model.block.*;
import ghidra.program.model.symbol.*;
import ghidra.program.model.scalar.*;
import ghidra.program.model.listing.*;
import ghidra.program.database.data.DataTypeUtilities;
import ghidra.program.model.address.*;

public class ConvertSC3KLinuxVTableBytesToAddressArray extends GhidraScript {

    public void run() throws Exception {
    	SymbolTable symbolTable = currentProgram.getSymbolTable();
    	DataTypeManager dtm = currentProgram.getDataTypeManager();
    	Listing listing = currentProgram.getListing(); 
    	
    	DataType pointerDataType = getDataTypes("pointer")[0];
    	
    	SymbolIterator iterator = symbolTable.getSymbolIterator();
    	
    	int pointerSize = currentProgram.getDefaultPointerSize();
    	
    	Symbol symbol = null;
    	
    	while ((symbol = iterator.next()) != null)
    	{
    		if (symbol.getSymbolType() == SymbolType.LABEL)
    		{
    			String name = symbol.getName();
    			
    			if (name.startsWith("__vt_"))
    			{
    				Address address = symbol.getAddress();
    				
    				Data data = getDataAt(address);
    				
    				if (data != null && data.isArray())
    				{
    					DataType dataType = data.getDataType();    					
      					
    					if (dataType.getName().startsWith("undefined1"))
    					{  						
    						int lengthAsPointer = dataType.getLength() / pointerSize;
    						
    						String typeName = "pointer[" + lengthAsPointer + "]";
    						
    						DataType[] types = getDataTypes(typeName);
    						DataType newDataType;
    						
    						if (types.length != 0)
    						{
    							newDataType = types[0];    							    						
    						}   						
    						else
    						{
    							ArrayDataType arrayDataType = new ArrayDataType(pointerDataType, lengthAsPointer);
    							
    							int transaction = dtm.startTransaction("Adding new data type");
    							newDataType = dtm.addDataType(arrayDataType, null);
    							dtm.endTransaction(transaction, true);
							}
    						
							println(name + ": replacing " + dataType.getName() + " with " + newDataType.getName());
							
							int newDataTypeSizeInBytes = newDataType.getLength();
							
							listing.clearCodeUnits(address, address.add(newDataTypeSizeInBytes), false);
							listing.createData(address, newDataType, newDataTypeSizeInBytes);
    					}
    				}
    				else
    				{
    					println(name + ": is undefined");
    				}
    			}
    		}
    	}
    }    
}
