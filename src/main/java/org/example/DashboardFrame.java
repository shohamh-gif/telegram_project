package org.example;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private DefaultTableModel tableModel;
    private JLabel totalMembersLabel;

    private JButton tabCommunityBtn;
    private JButton tabCreateBtn;
    private JButton tabLiveBtn;
    private JButton tabResultsBtn;
    private String activeTab;

    private DefaultTableModel liveStatusTableModel;
    private JLabel liveParticipantsValue;
    private JLabel liveCompletedValue;
    private JLabel liveRemainingValue;
    private JLabel liveTimeLeftLabel;
    private JLabel liveTitleLabel;

    private JPanel resultsContentPanel;
    private List<ActiveSurveySession.QuestionResult> lastResults;

    @Getter
    @Setter
    private MyBot bot;

    public static final String VIEW_DASHBOARD = "Dashboard";
    public static final String VIEW_SURVEY = "Survey";
    public static final String VIEW_LIVE_STATUS = "LiveStatus";
    public static final String VIEW_RESULTS = "Results";
    public static final String MEMBERS_PREFIX = "סה\"כ חברים בקהילה: ";
    public static final String FONT_NAME = "Segoe UI";

    // --- פלטה מאוזנת: וורוד = אקצנט בלבד (באנר, כפתור עיקרי, מצב פעיל), טורקיז = אקצנט משני ---
    public static final Color MAIN_BG_COLOR = new Color(250, 247, 241);   // קרם חמים ונייטרלי
    public static final Color BORDER_COLOR = new Color(226, 217, 205);   // חום-בז' עדין
    public static final Color BUTTON_COLOR = new Color(122, 155, 108);   // ירוק שני - אקצנט משני
    public static final Color DARK_TEXT = new Color(64, 56, 48);         // חום כהה חם, לא אפור
    public static final Color HEADER_COLOR = new Color(214, 188, 156);  // חום-בז' בהיר - שימוש עדין בלבד
    public static final Color ACCENT_COLOR = new Color(150, 98, 62);    // חום טרה-קוטה עמוק - אקצנט ראשי
    public static final Color TAB_UNDERLINE_ACTIVE = new Color(150, 98, 62);
    public static final Color TAB_BAR_BG = Color.WHITE;
    public static final Color TAB_TEXT_INACTIVE = new Color(155, 145, 148);
    public static final Color PROGRESS_DONE_COLOR = new Color(107, 142, 96);  // ירוק שלווה - גם "הושלם" וגם אקצנט
    public static final Color STATUS_PENDING_COLOR = new Color(219, 212, 202);
    public static final Color PROGRESS_ACTIVE_COLOR = new Color(196, 156, 92); // זהוב-חרדל - גיוון שלישי
    public static final Color TRACK_GRAY = new Color(238, 233, 226);

    public DashboardFrame() {
        setupModernPopups();
        this.setTitle("Telegram Survey Bot - לוח בקרה");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width, screenSize.height);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(this.createHeaderAndTabs(), BorderLayout.NORTH);

        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(this.cardLayout);
        this.mainContainer.add(this.createDashboardPanel(), VIEW_DASHBOARD);

        this.mainContainer.add(new CreateSurveyPanel(this), VIEW_SURVEY);
        this.mainContainer.add(this.createLiveStatusPanel(), VIEW_LIVE_STATUS);
        this.mainContainer.add(this.createResultsPanel(), VIEW_RESULTS);
        root.add(this.mainContainer, BorderLayout.CENTER);

        this.setContentPane(root);
        this.switchTab(VIEW_DASHBOARD);
    }

    private JPanel createHeaderAndTabs() {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));

        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(ACCENT_COLOR); // תוקן: היה HEADER_COLOR (בהיר מדי) עם טקסט לבן - לא קריא
        banner.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Telegram Survey Bot  -  לוח בקרה");
        title.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel titleWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleWrap.setOpaque(false);
        titleWrap.add(title);

        banner.add(titleWrap, BorderLayout.WEST);
        wrapper.add(banner);

        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(TAB_BAR_BG);
        tabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        this.tabCommunityBtn = this.createTabButton("קהילה");
        this.tabCreateBtn = this.createTabButton("יצירת סקר");
        this.tabLiveBtn = this.createTabButton("סקר פעיל");
        this.tabResultsBtn = this.createTabButton("תוצאות");

        this.tabCommunityBtn.addActionListener(e -> this.switchTab(VIEW_DASHBOARD));
        this.tabCreateBtn.addActionListener(e -> this.switchTab(VIEW_SURVEY));
        this.tabLiveBtn.addActionListener(e -> this.switchTab(VIEW_LIVE_STATUS));
        this.tabResultsBtn.addActionListener(e -> this.switchTab(VIEW_RESULTS));

        tabBar.add(this.tabCommunityBtn);
        tabBar.add(this.tabCreateBtn);
        tabBar.add(this.tabLiveBtn);
        tabBar.add(this.tabResultsBtn);

        wrapper.add(tabBar);
        return wrapper;
    }

    private JButton createTabButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        btn.setForeground(TAB_TEXT_INACTIVE);
        btn.setBackground(TAB_BAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(14, 22, 11, 22),
                BorderFactory.createMatteBorder(0, 0, 3, 0, TAB_BAR_BG)
        ));
        return btn;
    }

    public void switchTab(String viewKey) {
        this.activeTab = viewKey;
        this.cardLayout.show(this.mainContainer, viewKey);

        this.styleTab(this.tabCommunityBtn, VIEW_DASHBOARD.equals(viewKey), VectorIcons.user(ACCENT_COLOR, 18), VectorIcons.user(TAB_TEXT_INACTIVE, 18));
        this.styleTab(this.tabCreateBtn, VIEW_SURVEY.equals(viewKey), VectorIcons.plus(ACCENT_COLOR, 18), VectorIcons.plus(TAB_TEXT_INACTIVE, 18));
        this.styleTab(this.tabLiveBtn, VIEW_LIVE_STATUS.equals(viewKey), VectorIcons.play(ACCENT_COLOR, 18), VectorIcons.play(TAB_TEXT_INACTIVE, 18));
        this.styleTab(this.tabResultsBtn, VIEW_RESULTS.equals(viewKey), VectorIcons.chart(ACCENT_COLOR, 18), VectorIcons.chart(TAB_TEXT_INACTIVE, 18));
    }

    private void styleTab(JButton tab, boolean active, Icon activeIcon, Icon inactiveIcon) {
        tab.setForeground(active ? ACCENT_COLOR : TAB_TEXT_INACTIVE);
        tab.setIcon(active ? activeIcon : inactiveIcon);
        tab.setIconTextGap(10); // מרווח נקי ואסתטי בין האייקון לטקסט

        Color underline = active ? TAB_UNDERLINE_ACTIVE : TAB_BAR_BG;
        tab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(14, 22, 11, 22),
                BorderFactory.createMatteBorder(0, 0, 3, 0, underline)
        ));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(48);
        table.setFont(new Font(FONT_NAME, Font.PLAIN, 15));
        table.setForeground(DARK_TEXT);
        table.setSelectionBackground(new Color(250, 240, 243));
        table.setSelectionForeground(DARK_TEXT);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(FONT_NAME, Font.BOLD, 15));
        header.setBackground(Color.WHITE);
        header.setForeground(ACCENT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(100, 45));
        header.setReorderingAllowed(false);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(MAIN_BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel cardPanel = new JPanel(new BorderLayout(0, 15));
        cardPanel.setBackground(Color.WHITE); // הכרטיסייה נשארת לבנה ואטומה גם מעל תמונת רקע
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        this.totalMembersLabel = new JLabel(MEMBERS_PREFIX + "0");
        this.totalMembersLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        this.totalMembersLabel.setForeground(DARK_TEXT);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(this.totalMembersLabel, BorderLayout.EAST);

        cardPanel.add(topPanel, BorderLayout.NORTH);

        JScrollPane tableScroll = this.createTablePanel();
        tableScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        cardPanel.add(tableScroll, BorderLayout.CENTER);

        panel.add(cardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"מועד הצטרפות", "Telegram Username", "שם מלא"};
        this.tableModel = new NonEditableTableModel(columnNames, 0);

        JTable usersTable = new JTable(this.tableModel);
        this.styleTable(usersTable);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < usersTable.getColumnCount(); i++) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    public void addUserToTable(CommunityUser user) {
        SwingUtilities.invokeLater(() -> {
            String username = user.getTelegramUsername() != null ? "@" + user.getTelegramUsername() : "-";
            Object[] rowData = {user.getFormattedJoinTime(), username, user.getFirstName()};
            this.tableModel.addRow(rowData);
            this.totalMembersLabel.setText(MEMBERS_PREFIX + this.tableModel.getRowCount());
        });
    }

    private JPanel createLiveStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(MAIN_BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        this.liveTitleLabel = new JLabel("אין סקר פעיל כרגע", SwingConstants.CENTER);
        this.liveTitleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        this.liveTitleLabel.setForeground(ACCENT_COLOR);
        panel.add(this.liveTitleLabel, BorderLayout.NORTH);

        JPanel centerStack = new JPanel(new BorderLayout(14, 14));
        centerStack.setOpaque(false); // שקוף - כדי שתמונת/צבע הרקע יעברו דרכו
        JPanel cardsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsRow.setOpaque(false);
        this.liveParticipantsValue = new JLabel("0", SwingConstants.CENTER);
        this.liveCompletedValue = new JLabel("0", SwingConstants.CENTER);
        this.liveRemainingValue = new JLabel("0", SwingConstants.CENTER);

        cardsRow.add(this.createStatCard(this.liveParticipantsValue, "סה\"כ משתתפים", VectorIcons.user(PROGRESS_ACTIVE_COLOR, 28)));
        cardsRow.add(this.createStatCard(this.liveCompletedValue, "סיימו", VectorIcons.check(PROGRESS_DONE_COLOR, 28)));
        cardsRow.add(this.createStatCard(this.liveRemainingValue, "טרם סיימו", VectorIcons.dot(STATUS_PENDING_COLOR, 28)));

        this.liveTimeLeftLabel = new JLabel("זמן שנותר: 05:00", SwingConstants.CENTER);
        this.liveTimeLeftLabel.setFont(new Font(FONT_NAME, Font.BOLD, 18));
        this.liveTimeLeftLabel.setForeground(Color.WHITE);
        this.liveTimeLeftLabel.setOpaque(true);
        this.liveTimeLeftLabel.setBackground(ACCENT_COLOR); // תוקן: היה HEADER_COLOR (בהיר מדי) עם טקסט לבן
        this.liveTimeLeftLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.liveTimeLeftLabel.setVisible(false);

        centerStack.add(cardsRow, BorderLayout.NORTH);
        centerStack.add(this.liveTimeLeftLabel, BorderLayout.SOUTH);

        String[] columnNames = {"שם", "התקדמות", "סטטוס"};
        this.liveStatusTableModel = new NonEditableTableModel(columnNames, 0);
        JTable liveTable = new JTable(this.liveStatusTableModel);

        this.styleTable(liveTable);

        StatusRowRenderer statusRenderer = new StatusRowRenderer();
        for (int i = 0; i < liveTable.getColumnCount(); i++) {
            liveTable.getColumnModel().getColumn(i).setCellRenderer(statusRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(liveTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        centerStack.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerStack, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(JLabel valueLabel, String caption, Icon cardIcon) {
        // יצירת הכרטיסייה עם פינות מעוגלות רכות
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 10, 22, 10));

        JLabel iconLabel = new JLabel(cardIcon);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(new Font(FONT_NAME, Font.BOLD, 36)); // פונט קצת יותר דרמטי ומרשים למספרים
        valueLabel.setForeground(ACCENT_COLOR);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel captionLabel = new JLabel(caption, SwingConstants.CENTER);
        captionLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 15));
        captionLabel.setForeground(DARK_TEXT);
        captionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(captionLabel);
        return card;
    }

    public void onSurveyStarted(ActiveSurveySession session) {
        SwingUtilities.invokeLater(() -> {
            this.liveTitleLabel.setText("סקר פעיל");
            this.liveTitleLabel.setIcon(VectorIcons.dot(PROGRESS_DONE_COLOR, 14));
            this.liveTitleLabel.setIconTextGap(10);
            this.liveTimeLeftLabel.setVisible(true);
            this.switchTab(VIEW_LIVE_STATUS);
        });
    }

    public void updateSurveyStatus(ActiveSurveySession.SurveyStatusSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> {
            this.liveStatusTableModel.setRowCount(0);
            for (ActiveSurveySession.ParticipantProgress p : snapshot.progressList) {
                String statusText;
                switch (p.status) {
                    case COMPLETED:
                        statusText = "השלים";
                        break;
                    case IN_PROGRESS:
                        statusText = "בתהליך";
                        break;
                    default:
                        statusText = "טרם ענה";
                }
                Object[] row = {p.user.getFirstName(), p.answered + "/" + p.total, statusText};
                this.liveStatusTableModel.addRow(row);
            }

            this.liveParticipantsValue.setText(String.valueOf(snapshot.totalParticipants));
            this.liveCompletedValue.setText(String.valueOf(snapshot.completedCount));
            this.liveRemainingValue.setText(String.valueOf(snapshot.totalParticipants - snapshot.completedCount));

            long totalSeconds = snapshot.remainingMs / 1000;
            long mins = totalSeconds / 60;
            long secs = totalSeconds % 60;
            this.liveTimeLeftLabel.setText(String.format("זמן שנותר: %02d:%02d", mins, secs));
        });
    }

    private JPanel createResultsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(MAIN_BG_COLOR);
        outer.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        this.resultsContentPanel = new JPanel();
        this.resultsContentPanel.setLayout(new BoxLayout(this.resultsContentPanel, BoxLayout.Y_AXIS));
        this.resultsContentPanel.setOpaque(false); // שקוף - הרקע (תמונה/צבע) עובר דרכו

        JScrollPane scrollPane = new JScrollPane(this.resultsContentPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); // חובה גם כאן, אחרת ה-viewport מכסה את הרקע
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));

        outer.add(scrollPane, BorderLayout.CENTER);

        this.renderEmptyResults();
        return outer;
    }

    private void renderEmptyResults() {
        this.resultsContentPanel.removeAll();
        JLabel emptyLabel = new JLabel("אין תוצאות להצגה עדיין - סקר טרם הסתיים", SwingConstants.CENTER);
        emptyLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 16));
        emptyLabel.setForeground(TAB_TEXT_INACTIVE);
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setBorder(BorderFactory.createEmptyBorder(60, 0, 0, 0));
        this.resultsContentPanel.add(emptyLabel);
        this.resultsContentPanel.revalidate();
        this.resultsContentPanel.repaint();
    }

    public void showSurveyResults(List<ActiveSurveySession.QuestionResult> results) {
        SwingUtilities.invokeLater(() -> {
            this.lastResults = results;
            this.liveTitleLabel.setText("אין סקר פעיל כרגע");
            this.liveTitleLabel.setIcon(null);
            this.liveTimeLeftLabel.setVisible(false);

            this.resultsContentPanel.removeAll();

            JLabel header = new JLabel("הסקר נסגר - הנה התוצאות", SwingConstants.CENTER);
            header.setFont(new Font(FONT_NAME, Font.BOLD, 22));
            header.setForeground(DARK_TEXT);
            header.setAlignmentX(Component.CENTER_ALIGNMENT);
            this.resultsContentPanel.add(header);
            this.resultsContentPanel.add(Box.createVerticalStrut(18));

            int qNum = 1;
            for (ActiveSurveySession.QuestionResult qr : results) {
                JPanel qPanel = this.buildQuestionResultPanel(qNum, qr);
                qPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                this.resultsContentPanel.add(qPanel);
                this.resultsContentPanel.add(Box.createVerticalStrut(18));
                qNum++;
            }

            this.resultsContentPanel.revalidate();
            this.resultsContentPanel.repaint();
            this.switchTab(VIEW_RESULTS);
        });
    }

    private JPanel buildQuestionResultPanel(int qNum, ActiveSurveySession.QuestionResult qr) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        panel.setMaximumSize(new Dimension(760, 400));
        panel.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        JLabel qLabel = new JLabel("שאלה " + qNum + ": " + qr.question);
        qLabel.setFont(new Font(FONT_NAME, Font.BOLD, 17));
        qLabel.setForeground(DARK_TEXT);
        qLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel.add(qLabel);
        panel.add(Box.createVerticalStrut(12));

        int rank = 0;
        for (ActiveSurveySession.AnswerResult ar : qr.answers) {
            Color fillColor;
            if (ar.votes == 0) {
                fillColor = STATUS_PENDING_COLOR;
            } else if (rank == 0) {
                fillColor = PROGRESS_DONE_COLOR;
            } else {
                fillColor = PROGRESS_ACTIVE_COLOR;
            }

            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setBackground(Color.WHITE);
            row.setAlignmentX(Component.RIGHT_ALIGNMENT);
            row.setMaximumSize(new Dimension(720, 34));
            row.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

            JLabel answerLabel = new JLabel(ar.text);
            answerLabel.setFont(new Font(FONT_NAME, Font.BOLD, 13));
            answerLabel.setForeground(DARK_TEXT);
            answerLabel.setPreferredSize(new Dimension(150, 26));
            answerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            String barText = String.format("%.0f%%  (%d)", ar.percentage, ar.votes);
            PercentBarPanel bar = new PercentBarPanel(ar.percentage, barText, fillColor, TRACK_GRAY, FONT_NAME);
            bar.setPreferredSize(new Dimension(400, 30));

            row.add(answerLabel, BorderLayout.EAST);
            row.add(bar, BorderLayout.CENTER);
            panel.add(row);
            panel.add(Box.createVerticalStrut(6));
            rank++;
        }
        return panel;
    }

    public static void setupModernPopups() {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        UIManager.put("OptionPane.messageFont", new Font(FONT_NAME, Font.PLAIN, 15));
        UIManager.put("OptionPane.buttonFont", new Font(FONT_NAME, Font.BOLD, 14));
        UIManager.put("Button.background", BUTTON_COLOR);
        UIManager.put("Button.foreground", DARK_TEXT);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }
}