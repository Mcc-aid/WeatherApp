import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GradientPanel extends JPanel {
    private Color startColor, endColor;

    public GradientPanel(Color start, Color end) {
        startColor = start;
        endColor = end;
        setLayout(new BorderLayout());
    }

    public void setGradient(Color start, Color end) {
        startColor = start;
        endColor = end;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, startColor, getWidth(), getHeight(), endColor);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}

class RoundedPanel extends JPanel {
    private int cornerRadius = 20;

    public RoundedPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.dispose();
    }
}

public class WeatherGUI extends JFrame {
    private JComboBox<String> cityDropdown;
    private DefaultComboBoxModel<String> comboBoxModel;
    private JTextArea weatherOutput;
    private JScrollPane scrollPane;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private JScrollPane suggestionScroll;
    private JLabel weatherIconLabel;
    private GradientPanel backgroundPanel;
    private JButton fetchButton, themeToggleButton, toggleUnitsButton;
    private boolean isDarkTheme = false;
    private boolean isFahrenheit = true;
    private JPanel forecastCardsPanel;
    private JLabel cityTitle;
    private Map<String, String> conditionIconMap = new HashMap<>();

    public WeatherGUI() {
        setTitle("Weather App");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // background
        backgroundPanel = new GradientPanel(Color.CYAN, Color.WHITE);
        setContentPane(backgroundPanel);

        // bottom icon placeholder
        weatherIconLabel = new JLabel();
        weatherIconLabel.setHorizontalAlignment(JLabel.CENTER);
        // backgroundPanel.add(weatherIconLabel, BorderLayout.SOUTH);

        // input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(new Color(230, 240, 255));

        JLabel cityLabel = new JLabel("Enter City: ");
        comboBoxModel = new DefaultComboBoxModel<>();
        cityDropdown = new JComboBox<>(comboBoxModel);
        cityDropdown.setEditable(true);

        // suggestions popup
        suggestionPopup = new JPopupMenu();
        suggestionList = new JList<>();
        suggestionScroll = new JScrollPane(suggestionList);
        suggestionPopup.add(suggestionScroll);
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String sel = suggestionList.getSelectedValue();
                if (sel != null) {
                    cityDropdown.getEditor().setItem(sel);
                    suggestionPopup.setVisible(false);
                }
            }
        });
        ((JTextField) cityDropdown.getEditor().getEditorComponent()).getDocument()
                .addDocumentListener(new DocumentListener() {
                    public void insertUpdate(DocumentEvent e) {
                        updateSuggestions();
                    }

                    public void removeUpdate(DocumentEvent e) {
                        updateSuggestions();
                    }

                    public void changedUpdate(DocumentEvent e) {
                        updateSuggestions();
                    }

                    private void updateSuggestions() {
                        String q = ((JTextField) cityDropdown.getEditor().getEditorComponent()).getText();
                        if (q.length() < 2)
                            return;
                        new SwingWorker<List<String>, Void>() {
                            protected List<String> doInBackground() {
                                return CitySearchApi.getCitySuggestions(q);
                            }

                            protected void done() {
                                try {
                                    List<String> s = get();
                                    if (s.isEmpty())
                                        return;
                                    String typed = ((JTextField) cityDropdown.getEditor().getEditorComponent())
                                            .getText();
                                    comboBoxModel.removeAllElements();
                                    s.forEach(comboBoxModel::addElement);
                                    cityDropdown.setSelectedItem(typed);
                                    cityDropdown.showPopup();
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                }
                            }
                        }.execute();
                    }
                });

        fetchButton = new JButton("Get Weather");
        themeToggleButton = new JButton("Toggle Theme");
        toggleUnitsButton = new JButton("Switch to °C");
        Font bf = new Font("Segoe UI", Font.BOLD, 14);
        fetchButton.setFont(bf);
        themeToggleButton.setFont(bf);
        toggleUnitsButton.setFont(bf);

        themeToggleButton.addActionListener(e -> toggleTheme());

        // ** CHANGE ** reapply theme after unit toggle
        toggleUnitsButton.addActionListener(e -> {
            isFahrenheit = !isFahrenheit;
            toggleUnitsButton.setText(isFahrenheit ? "Switch to °C" : "Switch to °F");
            fetchButton.doClick();
            applyTheme(backgroundPanel, isDarkTheme);
        });

        inputPanel.add(cityLabel);
        inputPanel.add(cityDropdown);
        inputPanel.add(fetchButton);
        inputPanel.add(themeToggleButton);
        inputPanel.add(toggleUnitsButton);
        backgroundPanel.add(inputPanel, BorderLayout.NORTH);

        // title & output
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(10, 15, 5, 15));
        cityTitle = new JLabel("");
        cityTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titlePanel.add(cityTitle, BorderLayout.WEST);
        titlePanel.add(new JSeparator(), BorderLayout.SOUTH);

        weatherOutput = new JTextArea();
        weatherOutput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weatherOutput.setEditable(false);
        weatherOutput.setOpaque(false);
        weatherOutput.setLineWrap(true);
        weatherOutput.setWrapStyleWord(true);
        scrollPane = new JScrollPane(weatherOutput);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(10, 15, 10, 15));

        // forecast and info
        forecastCardsPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        forecastCardsPanel.setOpaque(false);
        forecastCardsPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel weatherInfoPanel = new JPanel(new GridLayout(2, 4, 20, 10));
        weatherInfoPanel.setOpaque(false);
        weatherInfoPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(titlePanel);
        contentPanel.add(weatherInfoPanel);
        contentPanel.add(scrollPane);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(contentPanel, BorderLayout.NORTH);
        JScrollPane cardScroll = new JScrollPane(forecastCardsPanel);
        cardScroll.setOpaque(false);
        cardScroll.getViewport().setOpaque(false);
        cardScroll.setBorder(null);
        cardScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        cardScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        centerPanel.add(cardScroll, BorderLayout.CENTER);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        initializeConditionIconMap();

        fetchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String city = (String) cityDropdown.getEditor().getItem();
                cityTitle.setText(city);
                if (city.isEmpty()) {
                    weatherOutput.setText("Please enter a city.");
                    return;
                }
                try {
                    WeatherData data = WeatherApi.getWeather(city);
                    String weekly = WeatherApi.getWeekForecast(city);

                    // info grid
                    weatherInfoPanel.removeAll();
                    weatherInfoPanel.add(createInfoBlock("Temperature",
                            isFahrenheit ? data.getTemperatureF() + "°F" : data.getTemperatureC() + "°C",
                            "icons/temp.png", true));
                    weatherInfoPanel.add(createInfoBlock("UV Index",
                            String.valueOf(data.getUvIndex()), "icons/uvindex.png", true));
                    weatherInfoPanel.add(createInfoBlock("Wind Speed",
                            data.getWindSpeedMph() + " mph", "icons/windspeed.png", true));
                    weatherInfoPanel.add(createInfoBlock("Sunrise",
                            data.getSunrise(), "icons/Sunrise.png", true));

                    // String feelsIconPath = null;
                    // for (Map.Entry<String,String> entry : conditionIconMap.entrySet()) {
                    // if (data.getConditionText().toLowerCase().contains(entry.getKey())) {
                    // feelsIconPath = entry.getValue();
                    // break;
                    // }
                    // }
                    // if (feelsIconPath == null) feelsIconPath = "icons/Sunny.png";
                    // weatherInfoPanel.add(createInfoBlock("Feels Like",
                    // isFahrenheit
                    // ? data.getFeelsLikeF()+"°F"
                    // : data.getFeelsLikeC()+"°C",
                    // feelsIconPath,
                    // true));

                    weatherInfoPanel.add(createInfoBlock("Feels Like",
                            isFahrenheit ? data.getFeelsLikeF() + "°F" : data.getFeelsLikeC() + "°C",
                            "icons/Sunny.png", true));
                    weatherInfoPanel.add(createInfoBlock("Humidity",
                            data.getHumidity() + "%", "icons/humidity.png", true));
                    weatherInfoPanel.add(createInfoBlock("Wind Direction",
                            data.getWindDirection(), "icons/windsock.png", true));
                    weatherInfoPanel.add(createInfoBlock("Sunset",
                            data.getSunset(), "icons/Sunset.png", true));
                    weatherInfoPanel.revalidate();
                    weatherInfoPanel.repaint();

                    // main icon
                    try {
                        String iconUrl = "http:" + data.getConditionIcon();
                        ImageIcon icon = new ImageIcon(new URL(iconUrl));
                        Image img = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                        weatherIconLabel.setIcon(new ImageIcon(img));
                    } catch (Exception ex) {
                        weatherIconLabel.setText("(No icon)");
                    }

                    // ** UPDATED LOOP **
                    forecastCardsPanel.removeAll();
                    String[] days = weekly.split("\\|");
                    for (String raw : days) {
                        String day = raw.trim();
                        if (day.isEmpty())
                            continue;
                        JPanel card = new RoundedPanel();
                        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                        card.setBackground(new Color(245, 245, 245, 140));
                        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                        JLabel text = new JLabel("<html>" +
                                formatForecastCards(day).replace(",", ",<br>") +
                                "</html>");
                        text.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        text.setAlignmentX(Component.CENTER_ALIGNMENT);
                        card.add(text);
                        try {
                            JLabel ico = getIconForCondition(day);
                            ico.setAlignmentX(Component.CENTER_ALIGNMENT);
                            card.add(ico);
                        } catch (Exception ex) {
                            card.add(Box.createRigidArea(new Dimension(48, 48)));
                        }
                        forecastCardsPanel.add(card);
                    }
                    forecastCardsPanel.revalidate();
                    forecastCardsPanel.repaint();
                    // reapply theme
                    applyTheme(backgroundPanel, isDarkTheme);

                } catch (Exception ex) {
                    weatherOutput.setText("Error fetching weather: " + ex.getMessage());
                }
            }
        });

        ((JTextField) cityDropdown.getEditor().getEditorComponent()).requestFocusInWindow();
        setVisible(true);
    }

    private void applyTheme(Component comp, boolean dark) {
        Color fg = dark ? Color.WHITE : Color.BLACK;
        Color bg = dark ? new Color(45, 45, 45) : Color.WHITE;

        // apply to labels, buttons, text fields, text areas, combo boxes
        if (comp instanceof JLabel
                || comp instanceof JButton
                || comp instanceof JTextField
                || comp instanceof JTextArea
                || comp instanceof JComboBox) {
            comp.setForeground(fg);
            comp.setBackground(bg); // <-- now sets text‑field bg too
        }

        // panels & scroll panes
        if (comp instanceof JPanel
                || comp instanceof JScrollPane) {
            comp.setBackground(bg);
            if (comp instanceof JScrollPane) {
                ((JScrollPane) comp).getViewport().setBackground(bg);
            }
        }

        // recurse
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                applyTheme(child, dark);
            }
        }
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        if (isDarkTheme)
            backgroundPanel.setGradient(new Color(30, 30, 30), new Color(70, 70, 70));
        else
            backgroundPanel.setGradient(Color.CYAN, Color.WHITE);
        applyTheme(backgroundPanel, isDarkTheme);
    }

    private String formatForecastCards(String forecastText) {
        try {
            String[] parts = forecastText.split(":");
            if (parts.length < 2)
                return forecastText;
            String hdr = parts[0].trim();
            String rest = parts[1].trim();
            String[] tc = rest.split(",", 2);
            String temps = tc[0].trim();
            String cond = tc.length > 1 ? tc[1].trim() : "";
            String[] range = temps.split("-");
            if (range.length < 2)
                return forecastText;
            int lowF = Integer.parseInt(range[0].replaceAll("[^\\d]", ""));
            int highF = Integer.parseInt(range[1].replaceAll("[^\\d]", ""));
            int lowC = (int) ((lowF - 32) * 5.0 / 9.0);
            int highC = (int) ((highF - 32) * 5.0 / 9.0);
            String disp = isFahrenheit
                    ? lowF + "°F - " + highF + "°F"
                    : lowC + "°C - " + highC + "°C";
            return hdr + ": " + disp + (cond.isEmpty() ? "" : ", " + cond);
        } catch (Exception e) {
            return forecastText;
        }
    }

    private void initializeConditionIconMap() {
        conditionIconMap.put("cloud", "icons/cloud.png");
        conditionIconMap.put("sun", "icons/Sunny.png");
        conditionIconMap.put("rain", "icons/rain.png");
        conditionIconMap.put("storm", "icons/stormyday.png");
        conditionIconMap.put("snow", "icons/snow.png");
        conditionIconMap.put("mist", "icons/mist.png");
        conditionIconMap.put("fog", "icons/foggy.png");
        conditionIconMap.put("drizzle", "icons/rain.png");
        conditionIconMap.put("clear", "icons/sun.png");
        conditionIconMap.put("thunder", "icons/stormyday.png");
        conditionIconMap.put("overcast", "icons/partlycloudy.png");
    }

    private JLabel getIconForCondition(String text) {
        String lower = text.toLowerCase();
        String iconPath = null;
        // your if/else logic here...
        if (iconPath == null) {
            for (Map.Entry<String, String> e : conditionIconMap.entrySet()) {
                if (lower.contains(e.getKey())) {
                    iconPath = e.getValue();
                    break;
                }
            }
        }
        if (iconPath == null)
            iconPath = "icons/sun.png";
        URL url = getClass().getResource("/" + iconPath);
        if (url == null)
            url = getClass().getResource("/icons/sun.png");
        ImageIcon ico = new ImageIcon(url);
        Image img = ico.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(img));
    }

    private JPanel createInfoBlock(String labelText, String valueText, String iconPath, boolean iconOnTop) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (iconPath != null && !iconPath.isEmpty()) {
            URL url = getClass().getResource("/" + iconPath);
            if (url != null) {
                ImageIcon ico = new ImageIcon(url);
                Image img = ico.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                JLabel l = new JLabel(new ImageIcon(img));
                l.setAlignmentX(Component.CENTER_ALIGNMENT);
                block.add(l);
            }
        }
        if (labelText != null && !labelText.isEmpty()) {
            JLabel l = new JLabel(labelText);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            block.add(l);
        }
        JLabel val = new JLabel(valueText);
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);
        block.add(val);
        return block;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherGUI());
    }
}
