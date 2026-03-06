//List the SimCity 3000 Unlimited Linux VTable names
//@author 0xC0000054 
//@category Symbol
//@keybinding 
//@menupath 
//@toolbar 
//@runtime Java

import ghidra.app.script.GhidraScript;
import ghidra.program.model.symbol.Symbol;
import ghidra.program.model.symbol.SymbolIterator;
import ghidra.program.model.symbol.SymbolTable;
import ghidra.program.model.symbol.SymbolType;

import java.util.SortedSet;
import java.util.TreeSet;

public class ListSC3KLinuxVTableNames extends GhidraScript {

    public void run() throws Exception {
    	SymbolTable symbolTable = currentProgram.getSymbolTable();    	
    	
    	SymbolIterator iterator = symbolTable.getSymbolIterator();   	
   	
    	Symbol symbol = null;
    	SortedSet<String> set = new TreeSet<>();
    	
    	while ((symbol = iterator.next()) != null)
    	{
    		if (symbol.getSymbolType() == SymbolType.LABEL)
    		{
    			String name = symbol.getName();
    			
    			if (name.startsWith("__vt_"))
    			{
    				set.add(name);
    			}
    		}
    	}
    	
    	for (String name : set)
    	{
    		println(name);
    	}
    }

}
