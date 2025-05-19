package tcc.etec.franco.dstarde.befghl.safewayapp.ui.Autoridades;

public class Authority {
    private String name;
    private int iconResId;
    private String phone;
    private String description;

    // Construtor
    public Authority(String name, int iconResId, String phone, String description) {
        this.name = name;
        this.iconResId = iconResId;
        this.phone = phone;
        this.description = description;
    }

    // Getters
    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public String getPhone() { return phone; }
    public String getDescription() { return description; }
}

