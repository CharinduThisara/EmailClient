public class OfficialRecipient extends AbsRecipient {
    private String designation;
    public OfficialRecipient(String type,String name,String email,String designation){
        super(type, name,email);
        this.designation=designation;
    }

    @Override
    protected String getDetails() {
        return " | Designation: "+this.designation;
    }
}
