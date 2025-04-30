package wue.eorc.umas.enums;

public enum ProcessingChain {

    AGISOFT("Agisoft Metashape"),
    TERRA("DJI Terra"),
    BOTH("Both above"),
    OTHER("Other");


    private final String name;

    ProcessingChain(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ProcessingChain fromName(String name){
        for(ProcessingChain processingChain : ProcessingChain.values()){
            if(processingChain.name.equals(name)){
                return processingChain;
            }
        }
        throw new IllegalArgumentException("Invalid ProcessingChain name: " + name);
    }


}
