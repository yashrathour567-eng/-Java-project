import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Cura — an all-in-one health consultation dashboard.
 * Pure Java desktop application; no external dependencies required.
 * Run: javac HealthConsult.java && java HealthConsult
 */
public class HealthConsult extends JFrame {
    private static final Color INK = new Color(29, 48, 57), MUTED = new Color(119, 137, 143);
    private static final Color TEAL = new Color(37, 139, 128), PALE = new Color(239, 247, 245);
    private static final Color BG = new Color(247, 249, 248), LINE = new Color(231, 237, 235);
    private final JPanel content = new JPanel(new BorderLayout());
    private final JPanel nav = new JPanel();
    private final java.util.List<Appointment> appointments = new ArrayList<>();
    private String role = "Patient", page = "Overview";
    private JLabel pageTitle;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new HealthConsult().setVisible(true);
        });
    }

    HealthConsult() {
        super("Cura — Care, connected");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setMinimumSize(new Dimension(1120, 740));
        setSize(1400, 900); setLocationRelativeTo(null); getContentPane().setBackground(BG);
        appointments.add(new Appointment("Dr. Maya Chen", "Cardiology", "Today · 10:30 AM", "Video", "Upcoming"));
        appointments.add(new Appointment("Dr. Elias Morgan", "General medicine", "Today · 2:00 PM", "In person", "Upcoming"));
        appointments.add(new Appointment("Dr. Sofia Patel", "Dermatology", "Oct 15 · 11:00 AM", "Video", "Upcoming"));
        buildShell(); showPage("Overview");
    }

    private void buildShell() {
        JPanel shell = new JPanel(new BorderLayout()); shell.setBackground(BG);
        JPanel sidebar = new JPanel(new BorderLayout()); sidebar.setPreferredSize(new Dimension(246, 0)); sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, LINE));
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0)); brand.setBackground(Color.WHITE); brand.setBorder(new EmptyBorder(28, 23, 27, 12));
        JLabel mark = new JLabel("✚"); mark.setOpaque(true); mark.setBackground(PALE); mark.setForeground(TEAL); mark.setFont(new Font("SansSerif", Font.BOLD, 22)); mark.setHorizontalAlignment(SwingConstants.CENTER); mark.setPreferredSize(new Dimension(38,38));
        JLabel word = new JLabel("cura"); word.setFont(new Font("SansSerif", Font.BOLD, 24)); word.setForeground(INK);
        brand.add(mark); brand.add(word); sidebar.add(brand, BorderLayout.NORTH);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS)); nav.setBackground(Color.WHITE); nav.setBorder(new EmptyBorder(3, 14, 10, 14)); sidebar.add(nav, BorderLayout.CENTER);
        JPanel profile = new JPanel(new BorderLayout(10, 0)); profile.setBackground(Color.WHITE); profile.setBorder(new EmptyBorder(15, 17, 19, 15));
        JLabel avatar = avatar("AM", 40); JPanel person = new JPanel(new GridLayout(2,1)); person.setBackground(Color.WHITE);
        person.add(label("Alex Morgan", 13, INK, true)); person.add(label("Patient account", 11, MUTED, false)); profile.add(avatar, BorderLayout.WEST); profile.add(person, BorderLayout.CENTER);
        sidebar.add(profile, BorderLayout.SOUTH);
        JPanel main = new JPanel(new BorderLayout()); main.setBackground(BG);
        main.add(buildTopbar(), BorderLayout.NORTH); content.setBackground(BG); main.add(content, BorderLayout.CENTER);
        shell.add(sidebar, BorderLayout.WEST); shell.add(main, BorderLayout.CENTER); setContentPane(shell); updateNav();
    }

    private JPanel buildTopbar() {
        JPanel bar = new JPanel(new BorderLayout()); bar.setBackground(Color.WHITE); bar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,LINE));
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); left.setBackground(Color.WHITE); left.setBorder(new EmptyBorder(21, 32, 19, 0));
        pageTitle = label("Overview", 20, INK, true); left.add(pageTitle); bar.add(left, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0)); right.setBackground(Color.WHITE); right.setBorder(new EmptyBorder(17, 0, 15, 28));
        JLabel today = label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, MMM d")), 12, MUTED, false); today.setBorder(new EmptyBorder(0,0,0,8)); right.add(today);
        JButton bell = new JButton("♧"); styleQuiet(bell); bell.setToolTipText("Notifications"); bell.addActionListener(e -> notice("You're all caught up.")); right.add(bell);
        JComboBox<String> roles = new JComboBox<>(new String[]{"Patient", "Healthcare professional", "Admin"}); roles.setSelectedItem(role); roles.setFont(new Font("SansSerif", Font.PLAIN, 12));
        roles.setBackground(Color.WHITE); roles.addActionListener(e -> { role = (String) roles.getSelectedItem(); page = "Overview"; updateNav(); showPage(page); }); right.add(roles);
        bar.add(right, BorderLayout.EAST); return bar;
    }

    private void updateNav() {
        nav.removeAll(); String[] items = role.equals("Patient") ? new String[]{"Overview","Appointments","Health records","Medical advice","My profile"} : role.equals("Admin") ? new String[]{"Overview","User management","Appointments","System settings","Analytics"} : new String[]{"Overview","My schedule","Consultations","Patient records","Messages"};
        nav.add(sectionLabel("WORKSPACE"));
        for (String item : items) { JButton b = navButton(item, item.equals(page)); b.addActionListener(e -> showPage(item)); nav.add(b); nav.add(Box.createVerticalStrut(4)); }
        nav.add(Box.createVerticalGlue()); nav.add(sectionLabel("SUPPORT")); JButton help = navButton("Help & support", false); help.addActionListener(e -> notice("Our care team is here to help.")); nav.add(help);
        nav.revalidate(); nav.repaint();
    }

    private JButton navButton(String text, boolean active) {
        String icon = switch(text) { case "Overview" -> "⌂"; case "Appointments" -> "▦"; case "Health records", "Patient records" -> "▤"; case "Medical advice", "Consultations" -> "✧"; case "My profile", "User management" -> "◎"; case "System settings" -> "⚙"; case "Analytics" -> "▥"; case "My schedule" -> "◷"; case "Messages" -> "✉"; default -> "?"; };
        JButton b = new JButton(icon + "   " + text); b.setHorizontalAlignment(SwingConstants.LEFT); b.setFont(new Font("SansSerif", Font.PLAIN, 13)); b.setFocusPainted(false); b.setBorder(new EmptyBorder(11, 12, 11, 8)); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setBackground(active ? PALE : Color.WHITE); b.setForeground(active ? TEAL : new Color(91, 109, 115)); b.setOpaque(true); b.setBorderPainted(false); return b;
    }
    private JLabel sectionLabel(String s) { JLabel l = label(s, 10, new Color(157,170,172), true); l.setBorder(new EmptyBorder(20, 12, 10, 0)); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l; }

    private void showPage(String p) {
        page = p; pageTitle.setText(p); updateNav(); content.removeAll();
        JPanel wrap = new JPanel(new BorderLayout()); wrap.setBackground(BG); wrap.setBorder(new EmptyBorder(25, 32, 28, 32));
        JPanel body = new JPanel(); body.setOpaque(false); body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        if (p.equals("Overview")) buildOverview(body);
        else switch(p) {
            case "Appointments" -> buildAppointments(body);
            case "Health records", "Patient records" -> buildRecords(body);
            case "Medical advice", "Consultations" -> buildAdvice(body);
            case "My schedule" -> buildSchedule(body);
            case "Messages" -> buildMessages(body);
            case "My profile" -> buildProfile(body);
            case "User management" -> buildUsers(body);
            case "System settings" -> buildSettings(body);
            case "Analytics" -> buildAnalytics(body);
            default -> buildOverview(body);
        }
        wrap.add(body, BorderLayout.NORTH); JScrollPane scroll = new JScrollPane(wrap); scroll.setBorder(null); scroll.getViewport().setBackground(BG); scroll.getVerticalScrollBar().setUnitIncrement(16); content.add(scroll); content.revalidate(); content.repaint();
    }

    private void buildOverview(JPanel body) {
        JPanel intro = new JPanel(new BorderLayout()); intro.setOpaque(false); intro.setBorder(new EmptyBorder(0,0,20,0));
        JPanel hi = new JPanel(); hi.setOpaque(false); hi.setLayout(new BoxLayout(hi, BoxLayout.Y_AXIS)); hi.add(label(greeting(), 24, INK, true)); hi.add(Box.createVerticalStrut(6)); hi.add(label(role.equals("Admin") ? "Here's what's happening across Cura today." : role.startsWith("Healthcare") ? "Your care team workspace is ready." : "Your health, all in one place. Here's your care at a glance.", 13, MUTED, false)); intro.add(hi, BorderLayout.WEST);
        JButton action = primary(role.equals("Patient") ? "+  Book appointment" : role.equals("Admin") ? "+  Add user" : "+  Set availability"); action.addActionListener(e -> { if(role.equals("Patient")) bookingDialog(); else if(role.equals("Admin")) userDialog(); else showPage("My schedule"); }); intro.add(action, BorderLayout.EAST); body.add(intro);
        JPanel stats = new JPanel(new GridLayout(1,4,14,0)); stats.setOpaque(false); stats.setBorder(new EmptyBorder(0,0,20,0));
        if(role.equals("Patient")) { stats.add(statCard("UPCOMING VISITS","3","1 today","◷",TEAL)); stats.add(statCard("HEALTH RECORDS","8","All up to date","▤",new Color(113,125,188))); stats.add(statCard("CARE TEAM","4","Your trusted providers","✚",new Color(221,151,91))); stats.add(statCard("WELLNESS SCORE","92","Looking good","♡",new Color(83,166,130))); }
        else if(role.equals("Admin")) { stats.add(statCard("TOTAL USERS","1,284","+ 8% this month","◎",TEAL)); stats.add(statCard("APPOINTMENTS","96","12 today","▦",new Color(113,125,188))); stats.add(statCard("ACTIVE PROVIDERS","48","Across 12 specialties","✚",new Color(221,151,91))); stats.add(statCard("SATISFACTION","98%","+ 2% this month","♡",new Color(83,166,130))); }
        else { stats.add(statCard("TODAY'S VISITS","6","2 remaining","◷",TEAL)); stats.add(statCard("MY PATIENTS","124","8 new this week","◎",new Color(113,125,188))); stats.add(statCard("PENDING NOTES","3","Ready to complete","▤",new Color(221,151,91))); stats.add(statCard("AVAILABILITY","On","Next slot 3:30 PM","♡",new Color(83,166,130))); }
        body.add(stats);
        JPanel row = new JPanel(new GridLayout(1,2,16,0)); row.setOpaque(false); row.setBorder(new EmptyBorder(0,0,16,0));
        row.add(upcomingCard()); row.add(quickCard()); body.add(row);
        body.add(healthTip());
    }
    private String greeting() { int h = java.time.LocalTime.now().getHour(); return (h < 12 ? "Good morning" : h < 17 ? "Good afternoon" : "Good evening") + (role.equals("Admin") ? ", Jordan" : role.startsWith("Healthcare") ? ", Dr. Chen" : ", Alex") + " 👋"; }

    private JPanel statCard(String title, String value, String note, String icon, Color color) {
        JPanel c = card(); c.setLayout(new BorderLayout(0,8)); c.setBorder(new EmptyBorder(17,18,16,16));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false); JLabel t = label(title,10,MUTED,true); top.add(t,BorderLayout.WEST); JLabel ic = label(icon,17,color,true); top.add(ic,BorderLayout.EAST); c.add(top,BorderLayout.NORTH);
        JPanel mid = new JPanel(); mid.setOpaque(false); mid.setLayout(new BoxLayout(mid,BoxLayout.Y_AXIS)); mid.add(label(value,27,INK,true)); mid.add(Box.createVerticalStrut(3)); mid.add(label(note,11,MUTED,false)); c.add(mid,BorderLayout.CENTER); return c;
    }
    private JPanel upcomingCard() {
        JPanel c = card(); c.setLayout(new BorderLayout()); JPanel head = cardHeader("Upcoming appointments", "View all", () -> showPage("Appointments")); c.add(head,BorderLayout.NORTH);
        JPanel list = new JPanel(); list.setOpaque(false); list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        int n = Math.min(3, appointments.size()); for(int i=0;i<n;i++){ Appointment a=appointments.get(i); list.add(appointmentRow(a)); if(i<n-1) list.add(new JSeparator()); }
        c.add(list,BorderLayout.CENTER); return c;
    }
    private JPanel appointmentRow(Appointment a) {
        JPanel row = new JPanel(new BorderLayout(12,0)); row.setOpaque(false); row.setBorder(new EmptyBorder(13,18,13,18));
        JLabel date = label(a.when.split(" · ")[0].substring(0, Math.min(a.when.split(" · ")[0].length(), 3)).toUpperCase(),10,TEAL,true); date.setOpaque(true); date.setBackground(PALE); date.setHorizontalAlignment(SwingConstants.CENTER); date.setPreferredSize(new Dimension(43,38)); row.add(date,BorderLayout.WEST);
        JPanel info = new JPanel(new GridLayout(2,1,0,4)); info.setOpaque(false); info.add(label(a.doctor,13,INK,true)); info.add(label(a.specialty+"  ·  "+a.when,11,MUTED,false)); row.add(info,BorderLayout.CENTER);
        JLabel badge = pill(a.mode); row.add(badge,BorderLayout.EAST); return row;
    }
    private JPanel quickCard() {
        JPanel c=card(); c.setLayout(new BorderLayout()); c.add(cardHeader(role.equals("Admin")?"Platform activity":role.startsWith("Healthcare")?"Today's schedule":"Your health snapshot", "See details", () -> showPage(role.equals("Patient")?"Health records":role.equals("Admin")?"Analytics":"My schedule")),BorderLayout.NORTH);
        JPanel interior=new JPanel(); interior.setOpaque(false); interior.setLayout(new BoxLayout(interior,BoxLayout.Y_AXIS)); interior.setBorder(new EmptyBorder(2,18,12,18));
        if(role.equals("Patient")) { interior.add(progressRow("Annual checkup", "Completed", .84f, TEAL)); interior.add(progressRow("Medication review", "Due Oct 20", .55f, new Color(216,157,88))); interior.add(progressRow("Wellness goals", "3 of 4 complete", .75f, new Color(113,125,188))); }
        else if(role.equals("Admin")) { interior.add(progressRow("Appointments completed", "72 this week", .72f, TEAL)); interior.add(progressRow("New patient signups", "34 this week", .54f, new Color(113,125,188))); interior.add(progressRow("Provider availability", "84% filled", .84f, new Color(216,157,88))); }
        else { interior.add(progressRow("9:00 AM · Alex Morgan", "General consultation", .9f, TEAL)); interior.add(progressRow("10:30 AM · Priya Shah", "Follow-up visit", .8f, new Color(113,125,188))); interior.add(progressRow("11:15 AM · Sam Rivera", "New patient", .68f, new Color(216,157,88))); }
        c.add(interior,BorderLayout.CENTER); return c;
    }
    private JPanel progressRow(String title,String note,float value,Color color) { JPanel p=new JPanel(new BorderLayout(6,6)); p.setOpaque(false); p.setBorder(new EmptyBorder(11,0,7,0)); JPanel labels=new JPanel(new BorderLayout()); labels.setOpaque(false); labels.add(label(title,12,INK,true),BorderLayout.WEST); labels.add(label(note,11,MUTED,false),BorderLayout.EAST); p.add(labels,BorderLayout.NORTH); JProgressBar bar=new JProgressBar(0,100); bar.setValue((int)(value*100)); bar.setBorderPainted(false); bar.setForeground(color); bar.setBackground(new Color(239,243,242)); bar.setPreferredSize(new Dimension(100,6)); p.add(bar,BorderLayout.CENTER); return p; }
    private JPanel healthTip() { JPanel tip=new JPanel(new BorderLayout(14,0)); tip.setBackground(new Color(232,245,241)); tip.setBorder(new EmptyBorder(16,19,16,19)); JLabel icon=label("✧",22,TEAL,true); tip.add(icon,BorderLayout.WEST); JPanel text=new JPanel(); text.setOpaque(false); text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS)); text.add(label(role.equals("Patient")?"A little reminder for your wellbeing":"A note from Cura",13,INK,true)); text.add(Box.createVerticalStrut(5)); text.add(label(role.equals("Patient")?"Small steps add up. Take a moment today to stretch, hydrate, and check in with yourself.":"Your workspace is up to date. Keep care personal, clear, and connected.",12,new Color(91,119,117),false)); tip.add(text,BorderLayout.CENTER); return tip; }

    private void buildAppointments(JPanel b) { JPanel head=new JPanel(new BorderLayout()); head.setOpaque(false); head.setBorder(new EmptyBorder(0,0,18,0)); head.add(label(role.equals("Admin")?"Manage and review appointments across Cura.":"Stay on top of your care with a clear view of every visit.",13,MUTED,false),BorderLayout.WEST); JButton add=primary("+  Book appointment"); add.addActionListener(e->bookingDialog()); if(role.equals("Patient"))head.add(add,BorderLayout.EAST); b.add(head); b.add(appointmentTable()); }
    private JPanel appointmentTable() { JPanel c=card(); c.setLayout(new BorderLayout()); String[] cols={"PROVIDER","SPECIALTY","DATE & TIME","VISIT TYPE","STATUS",""}; JPanel header=new JPanel(new GridLayout(1,6)); header.setBackground(new Color(250,251,251)); header.setBorder(new EmptyBorder(12,16,12,16)); for(String x:cols)header.add(label(x,10,MUTED,true)); c.add(header,BorderLayout.NORTH); JPanel rows=new JPanel(); rows.setOpaque(false); rows.setLayout(new BoxLayout(rows,BoxLayout.Y_AXIS)); for(Appointment a:appointments){ JPanel r=new JPanel(new GridLayout(1,6)); r.setBackground(Color.WHITE); r.setBorder(new EmptyBorder(15,16,15,16)); r.add(label(a.doctor,12,INK,true));r.add(label(a.specialty,12,MUTED,false));r.add(label(a.when,12,INK,false));r.add(label(a.mode,12,MUTED,false));r.add(pill(a.status)); JButton more=new JButton("⋯");styleQuiet(more);more.addActionListener(e->notice("Appointment options are ready."));r.add(more);rows.add(r);rows.add(new JSeparator());} c.add(rows,BorderLayout.CENTER); return c; }
    private void buildRecords(JPanel b) { b.add(subtitle("Your health information, organized and easy to access.")); JPanel grid=new JPanel(new GridLayout(0,2,14,14));grid.setOpaque(false);grid.setBorder(new EmptyBorder(18,0,0,0)); String[][] records={{"♡","Allergies","Penicillin","Updated Sep 18"},{"▤","Medications","Vitamin D · 1000 IU","Updated Oct 02"},{"◉","Vitals","BP 118/76 · Pulse 72","Recorded Oct 04"},{"✚","Immunizations","Flu vaccine · 2026","Updated Sep 28"},{"▦","Lab results","Annual blood panel","Available Sep 21"},{"◎","Care team","4 trusted providers","View your team"}}; for(String[] x:records){JPanel c=card();c.setLayout(new BorderLayout(12,8));c.setBorder(new EmptyBorder(20,20,20,20));c.add(label(x[0],22,TEAL,true),BorderLayout.WEST);JPanel d=new JPanel();d.setOpaque(false);d.setLayout(new BoxLayout(d,BoxLayout.Y_AXIS));d.add(label(x[1],14,INK,true));d.add(Box.createVerticalStrut(8));d.add(label(x[2],13,new Color(75,92,97),false));d.add(Box.createVerticalStrut(4));d.add(label(x[3],11,MUTED,false));c.add(d);grid.add(c);} b.add(grid); JButton add=primary("+  Add health record");add.setAlignmentX(Component.LEFT_ALIGNMENT);add.addActionListener(e->simpleForm("Add health record",new String[]{"Record type","Details"}));b.add(Box.createVerticalStrut(18));b.add(add); }
    private void buildAdvice(JPanel b) { b.add(subtitle("Your care team’s guidance and consultation notes.")); JPanel panel=card();panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));panel.setBorder(new EmptyBorder(4,20,4,20));String[][] notes={{"Dr. Maya Chen","Cardiology · Oct 08","Your readings look stable. Continue your current routine and aim for a gentle 20-minute walk most days.","Follow-up in 3 months"},{"Dr. Sofia Patel","Dermatology · Oct 01","Apply the prescribed moisturizer twice daily. Please reach out if the irritation persists beyond two weeks.","Prescription attached"},{"Dr. Elias Morgan","General medicine · Sep 22","Your annual blood panel is within the expected range. Keep up the good work with hydration and sleep.","Lab results available"}};for(String[] n:notes){JPanel item=new JPanel(new BorderLayout(0,10));item.setOpaque(false);item.setBorder(new EmptyBorder(18,0,18,0));JPanel h=new JPanel(new BorderLayout());h.setOpaque(false);h.add(label(n[0],14,INK,true),BorderLayout.WEST);h.add(label(n[1],11,MUTED,false),BorderLayout.EAST);item.add(h,BorderLayout.NORTH);item.add(label(n[2],13,new Color(76,93,97),false),BorderLayout.CENTER);JLabel tag=pill("✧  "+n[3]);item.add(tag,BorderLayout.SOUTH);panel.add(item);panel.add(new JSeparator());}b.add(Box.createVerticalStrut(18));b.add(panel); }
    private void buildSchedule(JPanel b) { b.add(subtitle("Manage your availability and make room for the care your patients need."));JPanel c=card();c.setLayout(new BorderLayout());c.setBorder(new EmptyBorder(18,20,20,20));c.add(label("Weekly availability",16,INK,true),BorderLayout.NORTH);JPanel rows=new JPanel(new GridLayout(7,3,12,12));rows.setOpaque(false);String[] days={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};for(int i=0;i<7;i++){rows.add(label(days[i],13,INK,true));JCheckBox on=new JCheckBox("Available",i<5);on.setBackground(Color.WHITE);on.setFont(new Font("SansSerif",0,12));rows.add(on);JTextField hours=new JTextField(i<5?"9:00 AM  –  5:00 PM":"Not scheduled");hours.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),new EmptyBorder(7,9,7,9)));rows.add(hours);}c.add(rows);b.add(c);JButton save=primary("Save availability");save.setAlignmentX(Component.LEFT_ALIGNMENT);save.addActionListener(e->notice("Your availability has been updated."));b.add(Box.createVerticalStrut(16));b.add(save); }
    private void buildMessages(JPanel b){b.add(subtitle("A secure place to stay connected with your care team."));JPanel c=card();c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));String[][] m={{"AM","Alex Morgan","Thanks, doctor. I’ll follow the plan and check in next week.","10:42 AM"},{"PS","Priya Shah","Could you clarify whether I should take this with food?","Yesterday"},{"SR","Sam Rivera","I uploaded my previous test results for you to review.","Oct 10"}};for(String[]x:m){JPanel r=new JPanel(new BorderLayout(12,0));r.setOpaque(false);r.setBorder(new EmptyBorder(15,16,15,16));r.add(avatar(x[0],40),BorderLayout.WEST);JPanel d=new JPanel(new GridLayout(2,1,0,5));d.setOpaque(false);d.add(label(x[1],13,INK,true));d.add(label(x[2],12,MUTED,false));r.add(d);r.add(label(x[3],11,MUTED,false),BorderLayout.EAST);c.add(r);c.add(new JSeparator());}b.add(Box.createVerticalStrut(16));b.add(c);}
    private void buildProfile(JPanel b){b.add(subtitle("Keep your personal information current for a smoother care experience."));JPanel c=card();c.setLayout(new GridLayout(0,2,16,14));c.setBorder(new EmptyBorder(22,22,22,22));String[] labels={"First name","Last name","Email address","Phone number","Date of birth","Preferred pharmacy"};String[] values={"Alex","Morgan","alex.morgan@email.com","+1 (555) 014-8820","May 14, 1992","Greenwood Pharmacy"};for(int i=0;i<labels.length;i++){JPanel f=new JPanel(new BorderLayout(0,7));f.setOpaque(false);f.add(label(labels[i],11,MUTED,true),BorderLayout.NORTH);JTextField t=new JTextField(values[i]);t.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),new EmptyBorder(9,10,9,10)));f.add(t);c.add(f);}b.add(c);JButton save=primary("Save changes");save.setAlignmentX(Component.LEFT_ALIGNMENT);save.addActionListener(e->notice("Your profile has been updated."));b.add(Box.createVerticalStrut(16));b.add(save);}
    private void buildUsers(JPanel b){b.add(subtitle("Manage patient and provider accounts across the platform."));JButton add=primary("+  Add user");add.setAlignmentX(Component.LEFT_ALIGNMENT);add.addActionListener(e->userDialog());b.add(add);b.add(Box.createVerticalStrut(17));JPanel c=card();c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));String[][] u={{"MC","Dr. Maya Chen","maya.chen@cura.health","Healthcare professional","Active"},{"AM","Alex Morgan","alex.morgan@email.com","Patient","Active"},{"EP","Dr. Elias Morgan","elias.morgan@cura.health","Healthcare professional","Active"},{"PS","Priya Shah","priya.shah@email.com","Patient","Pending"},{"JM","Jordan Miles","jordan@cura.health","Admin","Active"}};for(String[]x:u){JPanel r=new JPanel(new GridLayout(1,5,10,0));r.setBackground(Color.WHITE);r.setBorder(new EmptyBorder(13,16,13,16));r.add(label(x[1],12,INK,true));r.add(label(x[2],12,MUTED,false));r.add(label(x[3],12,MUTED,false));r.add(pill(x[4]));JButton edit=new JButton("Manage");styleQuiet(edit);edit.addActionListener(e->notice("User management options opened."));r.add(edit);c.add(r);c.add(new JSeparator());}b.add(c);}
    private void buildSettings(JPanel b){b.add(subtitle("Configure the platform to support safe, seamless care."));JPanel c=card();c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));String[][] s={{"Appointment duration","Default length for new visits","30 minutes"},{"Booking window","How far ahead patients can book","60 days"},{"Appointment reminders","Send reminders before visits","On"},{"New account approval","Review provider registrations","On"},{"Maintenance mode","Temporarily pause new bookings","Off"}};for(String[]x:s){JPanel r=new JPanel(new BorderLayout(14,0));r.setOpaque(false);r.setBorder(new EmptyBorder(16,19,16,19));JPanel left=new JPanel(new GridLayout(2,1,0,5));left.setOpaque(false);left.add(label(x[0],13,INK,true));left.add(label(x[1],11,MUTED,false));r.add(left);if(x[2].equals("On")||x[2].equals("Off")){JToggleButton t=new JToggleButton(x[2],x[2].equals("On"));t.addActionListener(e->t.setText(t.isSelected()?"On":"Off"));r.add(t,BorderLayout.EAST);}else{JComboBox<String> cb=new JComboBox<>(new String[]{x[2],"15 minutes","45 minutes","90 days"});cb.setPreferredSize(new Dimension(150,32));r.add(cb,BorderLayout.EAST);}c.add(r);c.add(new JSeparator());}b.add(c);JButton save=primary("Save settings");save.setAlignmentX(Component.LEFT_ALIGNMENT);save.addActionListener(e->notice("System settings have been saved."));b.add(Box.createVerticalStrut(16));b.add(save);}
    private void buildAnalytics(JPanel b){b.add(subtitle("A live snapshot of engagement and care delivery."));JPanel stats=new JPanel(new GridLayout(1,4,14,0));stats.setOpaque(false);stats.add(statCard("APPOINTMENTS","428","This month · +12%","▦",TEAL));stats.add(statCard("NEW PATIENTS","86","This month · +8%","◎",new Color(113,125,188)));stats.add(statCard("COMPLETION RATE","94%","+ 3% vs last month","♡",new Color(83,166,130)));stats.add(statCard("AVG. RATING","4.9 / 5","From 216 reviews","✧",new Color(221,151,91)));b.add(stats);JPanel chart=card();chart.setLayout(new BorderLayout());chart.setBorder(new EmptyBorder(20,22,22,22));chart.add(label("Appointments over the last 7 days",15,INK,true),BorderLayout.NORTH);JPanel bars=new JPanel(new GridLayout(1,7,18,0));bars.setOpaque(false);String[] day={"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};int[] val={55,72,46,89,65,37,28};for(int i=0;i<7;i++){JPanel col=new JPanel(new BorderLayout());col.setOpaque(false);JLabel n=label(""+val[i],10,MUTED,false);n.setHorizontalAlignment(0);col.add(n,BorderLayout.NORTH);JProgressBar bar=new JProgressBar(0,100);bar.setValue(val[i]);bar.setOrientation(SwingConstants.VERTICAL);bar.setBorderPainted(false);bar.setForeground(i==3?TEAL:new Color(138,197,186));bar.setBackground(new Color(241,245,244));col.add(bar);JLabel d=label(day[i],10,MUTED,false);d.setHorizontalAlignment(0);col.add(d,BorderLayout.SOUTH);bars.add(col);}chart.add(bars);chart.setPreferredSize(new Dimension(400,280));b.add(Box.createVerticalStrut(16));b.add(chart);}

    private void bookingDialog(){JDialog d=new JDialog(this,"Book an appointment",true);d.setSize(460,500);d.setLocationRelativeTo(this);JPanel p=new JPanel();p.setBackground(Color.WHITE);p.setBorder(new EmptyBorder(24,28,24,28));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));p.add(label("Find the right care for you",21,INK,true));p.add(Box.createVerticalStrut(7));p.add(label("Choose a provider and a time that works.",12,MUTED,false));p.add(Box.createVerticalStrut(22));JComboBox<String> doctor=new JComboBox<>(new String[]{"Dr. Maya Chen · Cardiology","Dr. Elias Morgan · General medicine","Dr. Sofia Patel · Dermatology","Dr. Noah Williams · Pediatrics"});JTextField date=new JTextField(LocalDate.now().plusDays(1).toString());JComboBox<String> time=new JComboBox<>(new String[]{"9:00 AM","10:30 AM","11:00 AM","1:30 PM","2:00 PM","3:30 PM"});JComboBox<String> mode=new JComboBox<>(new String[]{"Video consultation","In-person visit"});field(p,"Healthcare professional",doctor);field(p,"Preferred date",date);field(p,"Available time",time);field(p,"Visit type",mode);JButton confirm=primary("Confirm appointment");confirm.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));confirm.addActionListener(e->{String name=((String)doctor.getSelectedItem()).split(" · ")[0];appointments.add(new Appointment(name,((String)doctor.getSelectedItem()).split(" · ")[1],date.getText()+" · "+time.getSelectedItem(),(String)mode.getSelectedItem(),"Upcoming"));d.dispose();notice("Your appointment is booked. We’ll see you soon!");showPage(page);});p.add(Box.createVerticalStrut(10));p.add(confirm);d.setContentPane(p);d.setVisible(true);}
    private void userDialog(){JDialog d=new JDialog(this,"Add a user",true);d.setSize(420,390);d.setLocationRelativeTo(this);JPanel p=new JPanel();p.setBackground(Color.WHITE);p.setBorder(new EmptyBorder(25,28,24,28));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));p.add(label("Create a Cura account",20,INK,true));p.add(Box.createVerticalStrut(18));JTextField name=new JTextField(),email=new JTextField();JComboBox<String> r=new JComboBox<>(new String[]{"Patient","Healthcare professional","Admin"});field(p,"Full name",name);field(p,"Email address",email);field(p,"Account role",r);JButton save=primary("Create account");save.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));save.addActionListener(e->{if(name.getText().isBlank()||email.getText().isBlank()){notice("Please enter a name and email address.");return;}d.dispose();notice("Account created for "+name.getText()+".");showPage(page);});p.add(Box.createVerticalStrut(12));p.add(save);d.setContentPane(p);d.setVisible(true);}
    private void simpleForm(String title,String[] names){JDialog d=new JDialog(this,title,true);d.setSize(400,300);d.setLocationRelativeTo(this);JPanel p=new JPanel();p.setBackground(Color.WHITE);p.setBorder(new EmptyBorder(25,28,24,28));p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));for(String n:names)field(p,n,new JTextField());JButton save=primary("Save record");save.addActionListener(e->{d.dispose();notice("Your health record has been saved.");});p.add(Box.createVerticalStrut(12));p.add(save);d.setContentPane(p);d.setVisible(true);}
    private void field(JPanel p,String name,JComponent input){JPanel f=new JPanel(new BorderLayout(0,6));f.setOpaque(false);f.setAlignmentX(Component.LEFT_ALIGNMENT);f.setMaximumSize(new Dimension(Integer.MAX_VALUE,67));f.add(label(name,11,MUTED,true),BorderLayout.NORTH);input.setFont(new Font("SansSerif",0,12));input.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),new EmptyBorder(8,9,8,9)));f.add(input);p.add(f);p.add(Box.createVerticalStrut(9));}
    private JPanel cardHeader(String title,String action,Runnable run){JPanel h=new JPanel(new BorderLayout());h.setOpaque(false);h.setBorder(new EmptyBorder(16,18,12,16));h.add(label(title,14,INK,true),BorderLayout.WEST);JButton b=new JButton(action+"  →");styleQuiet(b);b.setForeground(TEAL);b.addActionListener(e->run.run());h.add(b,BorderLayout.EAST);return h;}
    private JPanel subtitle(String text){JPanel p=new JPanel(new BorderLayout());p.setOpaque(false);p.setBorder(new EmptyBorder(0,0,16,0));p.add(label(text,13,MUTED,false));return p;}
    private JPanel card(){JPanel p=new JPanel();p.setBackground(Color.WHITE);p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LINE),new EmptyBorder(0,0,0,0)));return p;}
    private JLabel label(String s,int size,Color color,boolean bold){JLabel l=new JLabel(s);l.setFont(new Font("SansSerif",bold?Font.BOLD:Font.PLAIN,size));l.setForeground(color);return l;}
    private JLabel avatar(String initials,int size){JLabel l=label(initials,12,TEAL,true);l.setOpaque(true);l.setBackground(PALE);l.setHorizontalAlignment(0);l.setPreferredSize(new Dimension(size,size));return l;}
    private JLabel pill(String s){JLabel l=label(s,10,s.equals("Pending")?new Color(176,125,53):TEAL,true);l.setOpaque(true);l.setBackground(s.equals("Pending")?new Color(255,247,228):PALE);l.setBorder(new EmptyBorder(5,8,5,8));return l;}
    private JButton primary(String s){JButton b=new JButton(s);b.setFont(new Font("SansSerif",Font.BOLD,12));b.setForeground(Color.WHITE);b.setBackground(TEAL);b.setFocusPainted(false);b.setBorder(new EmptyBorder(11,16,11,16));b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));return b;}
    private void styleQuiet(JButton b){b.setFont(new Font("SansSerif",0,12));b.setForeground(MUTED);b.setBackground(Color.WHITE);b.setFocusPainted(false);b.setBorder(new EmptyBorder(7,9,7,9));}
    private void notice(String msg){JOptionPane.showMessageDialog(this,msg,"Cura",JOptionPane.INFORMATION_MESSAGE);}
    private static class Appointment{String doctor,specialty,when,mode,status;Appointment(String d,String s,String w,String m,String st){doctor=d;specialty=s;when=w;mode=m;status=st;}}
}
