package SymbolTable;

import java.util.ArrayList;

public class SymbolTable {
    private String name;
    private ArrayList<SymbolTableEntry> entries;
    public SymbolTable parentTable;

    // code generation
    public int scopeSize = 0;

    public SymbolTable(String name, SymbolTable parentTable) {
        this.name = name;
        this.parentTable = parentTable;
        entries = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<SymbolTableEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(ArrayList<SymbolTableEntry> entries) {
        this.entries = entries;
    }

    public void appendEntry(SymbolTableEntry entry) {
        this.entries.add(entry);
    }

    public void addAtBeginning(SymbolTableEntry entry) {
        this.entries.add(0, entry);
    }

    public SymbolTableEntry getEntryByName(String name) {
        for (SymbolTableEntry entry : this.entries) {
            if (entry.name.equals(name)) {
                return entry;
            }
        }
        return null;
    }

    public SymbolTableEntry getEntryByNameKind(String name, Kind kind) {
        for (SymbolTableEntry entry : this.entries) {
            if (entry.name.equals(name) && entry.kind == kind) {
                return entry;
            }
        }
        return null;
    }

    // return the first found symbol table entry index based on name and kind
    public int getEntryIndexByNameKind(String name, Kind kind) {
        for (int i = 0; i < this.entries.size(); i++) {
            if (this.entries.get(i).name.equals(name) && this.entries.get(i).kind == kind) {
                return i;
            }
        }

        return -1;
    }

    // replace entry with specific index
    public void replaceEntry(int index, SymbolTableEntry newEntry) {
        this.entries.set(index, newEntry);
    }

    @Override
    public String toString() {
        String result = String.format("%1$-10s| %2$-10s| %3$-10s| ", 
        							  "Table: " + this.name,
        							  "scope size: " + this.scopeSize,
        							  "table id: " + this.hashCode());
        
        if (this.parentTable != null) {
            result += String.format("Parent table: %1$-10s", this.parentTable.hashCode());
        }
        result += "\n";
        result += String.format("%1$-10s| %2$-10s| %3$-30s| %4$-10s| %5$-10s| %6$-10s", "name", "kind", "type", "link", "size", "offset");
        result += "\n----------------------------------------------------------------------------------------\n";
        for (SymbolTableEntry entry : this.entries) {
            result += String.format("%1$-10s| %2$-10s| ", entry.name, entry.kind);
            if (entry.type != null) {
                String temp = entry.type.name;
                if (entry.type.dimension.size() != 0) {
                    temp += "[" + String.join("][", entry.type.dimension) + "]";
                }
                result += String.format("%1$-30s| ", temp);
            } else if (entry.kind == Kind.function) {
                String temp = "(";
                for (SymbolTableEntryType input : entry.funcInputType) {
                    temp += input.name;
                    if (input.dimension.size() != 0) {
                        temp += "[" + String.join("][", input.dimension) + "]";
                    }
                    temp += ",";
                }
                temp += "):";
                temp += entry.funcOutputType.name;
                if (entry.funcOutputType.dimension.size() != 0) {
                    temp += "[" + String.join("][", entry.funcOutputType.dimension) + "]";
                }

                result += String.format("%1$-30s| ", temp);
            } else {
                result += String.format("%1$-30s| ", "");
            }

            if (entry.link != null) {
                result += String.format("%1$-10s| ", entry.link.hashCode());
            } else {
                result += String.format("%1$-10s| ", "");
            }

            result += String.format("%1$-10s| ", entry.size);
            result += String.format("%1$-10s| ", entry.offset);
            result += "\n___________________________________________________________________________________\n";
        }

        return result;
    }

    /**
     * look up entry with given name and kind within the input table as well as the parent tables
     */
    public static SymbolTableEntry lookupEntryInTableAndParentTable(SymbolTable table, String name, Kind kind) {
        while (table != null && table.getEntryByNameKind(name, kind) == null) {
            table = table.parentTable;
        }
        
        if (table != null) {
            return table.getEntryByNameKind(name, kind);
        }

        return null;
    }

    public static SymbolTableEntry lookupEntryInTableAndparentTable(SymbolTable table, String name, Kind[] kinds) {
        SymbolTableEntry result = null;
        
        for (Kind kind : kinds) {
            result = lookupEntryInTableAndParentTable(table, name, kind);
            if (result != null) {
                return result;
            }
        }

        return result;
    }
}