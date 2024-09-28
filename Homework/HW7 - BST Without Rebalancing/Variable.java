class Variable extends Base {  
        public String name;             /* name of variable */  
        public long value;              /* value of interest */  
        private Tracker tracker;  
  
        public Variable (String nm, long val, String caller) {  
                tracker = new Tracker("Variable",   
                        Size.of (name) + Size.of (value) + Size.of (tracker),  
                        caller + " calling Variable ctor");  
                name = nm;  
                value = val;  
        }  
        public Variable (Variable variable) {  
                tracker = new Tracker("Variable",   
                        Size.of (name) + Size.of (value) + Size.of (tracker),  
                        "Variable ctor");  
                name = new String (variable.name);  
                value = variable.value;  
        }  
  
        public void jettison() {  
                tracker.jettison();  
        }  
  
        public String getName () {  
                return name;  
        }  
        public boolean equals (Object object) {  
                if (this == object)  
                        return true;  
  
                if (!(object instanceof Variable))  
                        return false;  
                  
                Variable otherVar = (Variable) object;  
                  
                return name.equals (otherVar.getName ());  
        }  
        public boolean isLessThan (Base base) {  
                return (name.compareTo (base.getName ()) < 0) ? true : false;  
        }  
        public String toString () {  
                return name + "(" + value + ")";  
        }  
  
        public Variable assignValue (long val) {  
                  
                Variable retval;        // return value  
  
                // give variable its value  
                value = val;  
                retval = new Variable (this);  
  
                return retval;  
        }  
}  