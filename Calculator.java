import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Calculator extends JFrame {
	//GUI element declaration
	private JLabel displayFocus = new JLabel("Avali Personal Shield Generator");
	private ArrayList<JLabel> headerLabels = new ArrayList<JLabel>();
	private ButtonGroup selectors = new ButtonGroup();
	private JRadioButton selectRadioButton = new JRadioButton("Select weapons");
	private JRadioButton editRadioButton = new JRadioButton("Edit weapons");
	private ButtonGroup ballisticOptions = new ButtonGroup();
	private JRadioButton durationRadioButton = new JRadioButton("Use Duration (s)");
	private JRadioButton numberOfShotsRadioButton = new JRadioButton("Use Shots");
	private ArrayList<JLabel> bodyLabels = new ArrayList<JLabel>();
	private ArrayList<JLabel> footerLabels = new ArrayList<JLabel>();
	private ArrayList<JComboBox> comboBoxes = new ArrayList<JComboBox>();
	private ArrayList<JTextField> subInputBoxes = new ArrayList<JTextField>();
	private ArrayList<JTextField> inputBoxes = new ArrayList<JTextField>();
	private JButton applyAddButton = new JButton("APPLY DAMAGE");
	private JButton undoEditButton = new JButton("UNDO DAMAGE");
	private JButton cancelButton = new JButton("CANCEL");
	private JButton addButton = new JButton("ADD WEAPON"); //is also used to edit weapons
	private ArrayList<JSeparator> bars = new ArrayList<JSeparator>();
	private boolean lockThreads = false;
	private int bodyStartX, bodyStartY;
	
	//non-GUI items
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private File weaponProfiles = new File("WeaponProfiles.txt");
	
	//shield variables
	private double charge = 1706473223d;
	private double radius = 0.5d;
	private double currentTemp = 0d;
	private double maxTemp = 1024920d;
	private double overdriveOutput = 1024920d;
	private double stableOutput = 724210d;
	private ArrayList<String> warnings = new ArrayList<String>();
	private ArrayList<Double> tempLog = new ArrayList<Double>();
	private ArrayList<Double> chargeLog = new ArrayList<Double>();
	//next damage
	private double damage = 0d;
	private double tempIncrease = 0d;
	
	public Calculator() {
		setResizable(false);
		setTitle("Avali Shield Tracker");
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
		//window properties
		int windowWidth = 450, windowHeight = 600;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//CONFIGURE LAYOUT
		setLayout(null);
		
		int xPos = 0, yPos = 10; //used to track current position of items to add (avoids manual tracking)
		int buffer = 5; //buffer space between elements on the GUI
		int labelHeight = 15;
		
		//title (centered)
		this.displayFocus.setBounds((windowWidth/2)-(179/2),yPos,179,labelHeight);
		yPos += labelHeight + buffer;
		
		//separator
		bars.add(new JSeparator());
		bars.get(bars.size() - 1).setBounds(0,yPos,windowWidth,10);
		yPos += 10 + buffer;
		
		//header
		int labelWidth = 150;
		xPos = (windowWidth/2) - labelWidth;
		this.tempLog.add(this.currentTemp);
		this.chargeLog.add(this.charge);
		String[] headerLabelStrings = {"Charge Remaining: ", String.format("%.0f", this.charge) + " J", "Core Temperature: ", calculateTempPercent() + " %",  "Max Stable Active Time: ", "39 minutes, 16 seconds", "Estimated Active Time: ", "N/A"}; //store header labels in order
		for(int i = 0; i < headerLabelStrings.length; i++) {
			if(i % 2 == 0) { //move to create right side
				this.headerLabels.add(new JLabel(headerLabelStrings[i], SwingConstants.RIGHT));
				this.headerLabels.get(i).setBounds(xPos, yPos, labelWidth, labelHeight);
				xPos += labelWidth + 5;
			} else { //move to create left side
				this.headerLabels.add(new JLabel(headerLabelStrings[i]));
				this.headerLabels.get(i).setBounds(xPos, yPos, labelWidth, labelHeight);
				xPos = (windowWidth/2) - labelWidth;
				yPos += labelHeight + buffer;
			}
		}
		this.warnings.add("NONE");
		this.warnings.add("CORE TEMP");
		this.warnings.add("OVERHEAT");
		this.warnings.add("FAILURE");
		yPos += 10;
		
		//separator
		bars.add(new JSeparator());
		bars.get(bars.size() - 1).setBounds(0,yPos,windowWidth,10);
		yPos += buffer * 2;
		
		//body
		this.bodyStartX = xPos = 10;
		this.bodyStartY = yPos;
		labelWidth -= 5;
		//read in text file WeaponProfiles.txt and process contents
		getFileContents();
		//set up radio buttons
		this.selectors.add(selectRadioButton);
		this.selectors.add(editRadioButton);
		xPos = (windowWidth - xPos - labelWidth * 2) / 2;
		this.selectRadioButton.setBounds(xPos, yPos, labelWidth, labelHeight);
		this.selectRadioButton.setSelected(true);
		xPos += buffer + labelWidth;
		this.editRadioButton.setBounds(xPos, yPos, labelWidth, labelHeight);
		xPos = this.bodyStartX;
		yPos += labelHeight + buffer * 2;
		//separator
		bars.add(new JSeparator());
		bars.get(bars.size() - 1).setBounds(0,yPos,windowWidth,10);
		yPos += labelHeight + buffer;
		//set up labels
		labelWidth -= 20;
		String[] bodyLabelStrings = {"Type: ", "Name: ", "Ammo: ", "Muzzle Velocity (m/s): ", "Rate of Fire (rps): "};
		for(int i = 0; i < bodyLabelStrings.length; i++) {
			this.bodyLabels.add(new JLabel(bodyLabelStrings[i], SwingConstants.RIGHT));
			this.bodyLabels.get(i).setBounds(xPos, yPos, labelWidth, labelHeight);
			yPos += labelHeight + buffer * 3;
		}
		//set up combo boxes
		xPos = this.bodyStartX + labelWidth + buffer * 2;
		yPos = this.bodyStartY + labelHeight * 2 + buffer * 3;
		labelWidth += 60;
		for(int i = 0; i < 5; i++) {
			this.comboBoxes.add(new JComboBox());
			this.comboBoxes.get(i).setBounds(xPos, yPos - 2, labelWidth, labelHeight * 3 / 2);
			yPos += labelHeight + buffer * 3;
		}
		String[] weaponTypesList = {"Ballistic", "Explosive", "Melee", "Other", "Ammunition"};
		for(String s : weaponTypesList) {
			this.comboBoxes.get(0).addItem(s);
		}
		//combo box action listeners
		this.comboBoxes.get(0).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
				refreshBody(0);
            }
        });
		this.comboBoxes.get(1).addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                refreshBody(1);
            }
        });
		for(int i = 2; i < this.comboBoxes.size(); i++) {
			this.comboBoxes.get(i).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					updateFooter();
				}
			});
		}
		//comboBoxes.get(2) and .get(3) do not need action listeners because there are no dependencies for those comboboxes
		//set up sub text boxes for distance input for ballistic and explosive weapons; this box should be in the same place as the last combobox and the two slots below it
		yPos -= (labelHeight + buffer * 3);
		for(int i = 0; i < 3; i++) {
			this.subInputBoxes.add(new JTextField("1"));
			this.subInputBoxes.get(i).setBounds(xPos, yPos - 2, labelWidth, labelHeight * 3 / 2);
			this.subInputBoxes.get(i).setVisible(true);
			yPos += labelHeight + buffer * 3;
		}
		//set up text boxes
		xPos += labelWidth + buffer * 2;
		yPos = this.bodyStartY + labelHeight * 4 + buffer * 3;
		labelWidth = windowWidth - this.bodyLabels.get(0).getWidth() - buffer * 4 - this.comboBoxes.get(0).getWidth() - buffer * 4 - buffer * 4;
		for(int i = 0; i < 4; i++) {
			this.inputBoxes.add(new JTextField());
			this.inputBoxes.get(this.inputBoxes.size() - 1).setBounds(xPos, yPos - 1, labelWidth, labelHeight + 4);
			yPos += labelHeight + buffer * 3;
			this.inputBoxes.get(this.inputBoxes.size() - 1).setVisible(false);
		}
		//action listeners for text boxes used to call for footer updates
		for(int i = 0; i < this.subInputBoxes.size(); i++) {
			this.subInputBoxes.get(i).getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent evt) {
					updateFooter();
				}
				public void removeUpdate(DocumentEvent evt) {
					updateFooter();
				}
				public void insertUpdate(DocumentEvent evt) {
					updateFooter();
				}
			});
			//when a subInputBox has lost focus, check to see if it is blank; if it is, load a default value into it
			this.subInputBoxes.get(i).addFocusListener(new FocusListener() {
				public void focusLost(FocusEvent evt) {
					setSubInputBoxDefaults();
				}
				public void focusGained(FocusEvent evt) {}
			});
			//when an invalid key is pressed, the entry is not allowed as part of the text field (valid keys include numbers and a decimal point (but not more than one decimal point) delete, and backspace)
			this.subInputBoxes.get(i).addKeyListener(new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					char keyPressed = e.getKeyChar();
					if(((keyPressed < '0' || keyPressed > '9') && keyPressed != KeyEvent.VK_BACK_SPACE && keyPressed != KeyEvent.VK_DELETE) && !(keyPressed == '.' && !getCurrentBoxText().contains("."))) {
						e.consume();
					}
				}
			});
		}
		//set up ballistic option radio buttons
		xPos = 10;
		labelWidth = this.bodyLabels.get(0).getWidth();
		this.ballisticOptions.add(this.durationRadioButton);
		this.ballisticOptions.add(this.numberOfShotsRadioButton);
		this.numberOfShotsRadioButton.setBounds(xPos, yPos, labelWidth, labelHeight);
		this.numberOfShotsRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
		this.numberOfShotsRadioButton.setHorizontalTextPosition(JRadioButton.LEFT);
		this.numberOfShotsRadioButton.setSelected(true);
		yPos += labelHeight + buffer * 3;
		this.durationRadioButton.setBounds(xPos, yPos, labelWidth, labelHeight);
		this.durationRadioButton.setHorizontalAlignment(SwingConstants.RIGHT);
		this.durationRadioButton.setHorizontalTextPosition(JRadioButton.LEFT);
		yPos += labelHeight + buffer * 3;
		//radio button action listeners
		this.selectRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
				toggleInputBoxes(false);
            }
        });
		this.editRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
				toggleInputBoxes(true);
            }
        });
		this.durationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
				updateFooter();
            }
        });
		this.numberOfShotsRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
				updateFooter();
            }
        });
		//separator
		bars.add(new JSeparator());
		bars.get(bars.size() - 1).setBounds(0,yPos,windowWidth,10);
		yPos += labelHeight - 5 + 15;
		//footer
		//set up buttons
		xPos = 10;
		this.undoEditButton.setBounds(xPos, yPos - 15, labelWidth, labelHeight * 3);
		this.undoEditButton.setEnabled(false);
		xPos = windowWidth - labelWidth - buffer * 5;
		this.applyAddButton.setBounds(xPos, yPos - 15, labelWidth, labelHeight * 3);
		//footer labels
		xPos = 10 + labelWidth + buffer * 2;
		String[] footerLabelText = {"DAMAGE: ", "0 J", "TEMP INC: ", "0.0 %", "WARNING: ", "NONE"};
		int tempLabelWidth = labelWidth / 2;
		for(int i = 0; i < footerLabelText.length; i++) {
			this.footerLabels.add(new JLabel(footerLabelText[i]));
			if(i % 2 == 0) {
				this.footerLabels.get(i).setBounds(xPos, yPos - 20, tempLabelWidth, labelHeight);
				xPos += tempLabelWidth + buffer;
				this.footerLabels.get(i).setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				this.footerLabels.get(i).setBounds(xPos, yPos - 20, labelWidth, labelHeight);
				xPos = 10 + labelWidth + buffer * 2;
				yPos += buffer + labelHeight;
			}
		}
		yPos += labelHeight * 2 + buffer * 2 - 20;
		this.undoEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                undoEdit();
            }
        });
		this.applyAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                applyAdd();
            }
        });
		setPreferredSize(new Dimension(windowWidth, yPos));
		
		//set starting layout (ballistic profile)
		refreshBody(0); //0 is the box that changes should start below
		
		//add all components to the JFrame
		add(displayFocus);
		for(JSeparator j : bars) {
			add(j);
		}
		add(this.selectRadioButton);
		add(this.editRadioButton);
		for(JLabel j : this.headerLabels) {
			add(j);
		}
		for(JLabel j : this.bodyLabels) {
			add(j);
		}
		for(JLabel j : this.footerLabels) {
			add(j);
		}
		for(JComboBox j : this.comboBoxes) {
			add(j);
		}
		for(JTextField j : this.inputBoxes) {
			add(j);
		}
		for(JTextField j : this.subInputBoxes) {
			add(j);
		}
		add(this.durationRadioButton);
		add(this.numberOfShotsRadioButton);
		add(this.undoEditButton);
		add(this.applyAddButton);
        pack();
    }
	
	//set subInputBoxes to default values
	public void setSubInputBoxDefaults() {
		if(!this.lockThreads) {
			this.lockThreads = true;
			for(int i = 0; i < this.subInputBoxes.size(); i++) {
				//store text in double; if it string to double cast doesn't work, store 1 as the default value
				try {
					Double.valueOf(this.subInputBoxes.get(i).getText());
				} catch(NumberFormatException e) {
					this.subInputBoxes.get(i).setText("1");
				}
			}
			this.lockThreads = false;
			updateFooter();
		}
	}
	
	public String getCurrentBoxText() {
		for(int i = 0; i < this.subInputBoxes.size(); i++) {
			if(this.subInputBoxes.get(i).hasFocus()) {
				return this.subInputBoxes.get(i).getText();
			}
		}
		return "ERROR: NO TEXT FIELD SELECTED";
	}
	
	public String calculateTempPercent() {
		return String.valueOf(this.currentTemp / this.maxTemp);
	}
	
	//update footer based on selected content
	public void updateFooter() {
		//this is needed to prevent setSubInputBoxDefaults() from trying to update the footer each time a text field is set to a default value
		if(!this.lockThreads) {
			this.lockThreads = true;
			//do not update the footer if any text field is blank
			for(JTextField j : subInputBoxes) {
				if(j.getText().equals("")) {
					this.lockThreads = false;
					return;
				}
			}
			//calculate damage and update footer
			this.damage = 0d;
			switch(String.valueOf(this.comboBoxes.get(0).getSelectedItem())) {
				case "Ballistic":
					//get selected ammo mass
					double ammoMass = findAmmoMass();
					//get muzzle velocity OR ammo velocity if muzzle velocity is 0 m/s
					double muzzleVelocity = Double.valueOf(String.valueOf(this.comboBoxes.get(3).getSelectedItem()));
					if(muzzleVelocity == 0) {
						//get ammo velocity to use instead of muzzle velocity
						muzzleVelocity = findAmmoVelocity();
					}
					if(numberOfShotsRadioButton.isSelected()) { //KE = shots*0.5*m*v^2
						//calculate damage
						this.damage = Double.valueOf(this.subInputBoxes.get(1).getText()) * 0.5d * ammoMass * Math.pow(muzzleVelocity, 2);
					} else { //KE = RPS*duration*0.5*m*v^2
						this.damage = Double.valueOf(String.valueOf(this.comboBoxes.get(4).getSelectedItem())) * Double.valueOf(this.subInputBoxes.get(2).getText()) * 0.5d * ammoMass * Math.pow(muzzleVelocity, 2);
					}
				break;
				case "Explosive":
					if((String.valueOf(this.comboBoxes.get(3).getSelectedItem())).equals("Spherical")) {
						//spherical explosion
						//Damage(spherical) = (yield / (4 * pi * r^2)) * SA(shield)
						this.damage = (Double.valueOf(String.valueOf(this.comboBoxes.get(2).getSelectedItem())) / (4d * Math.PI * Math.pow(Double.valueOf(this.subInputBoxes.get(0).getText()), 2))) * (Math.PI * Math.pow(this.radius, 2));
					} else {
						//shaped explosion
						//Damage(shaped) = (yield / ((1/3) * pi * (d * sin(45))^2 * d)) * SA(avali)
						this.damage = (Double.valueOf(String.valueOf(this.comboBoxes.get(2).getSelectedItem())) / ((1d/3d) * Math.PI * Math.pow((Double.valueOf(this.subInputBoxes.get(0).getText()) * Math.sin(45d)), 2))) * (Math.PI * Math.pow(this.radius, 2));
					}
				break;
				case "Melee":
					//KE = 0.5 * m * v^2
					this.damage = 0.5d * Double.valueOf(String.valueOf(this.comboBoxes.get(2).getSelectedItem())) * Math.pow(Double.valueOf(String.valueOf(this.comboBoxes.get(3).getSelectedItem())), 2);
				break;
				case "Other":
					//KE = output
					this.damage = Double.valueOf(String.valueOf(this.comboBoxes.get(2).getSelectedItem()));
				break;
			}
			this.footerLabels.get(1).setText(String.valueOf(Math.round(this.damage)) + " J");
			//caluclate temp inc and update footer
			this.tempIncrease = (this.damage - this.stableOutput) * 100 / this.maxTemp; /***/
			if(this.tempIncrease < 0) {
				this.tempIncrease = 0;
			}
			this.footerLabels.get(3).setText(String.format("%.1f", this.tempIncrease) + " %");
			//display appropriate warnings
			if(this.charge - this.damage <= 0) {
				this.footerLabels.get(5).setText(this.warnings.get(3));
			} else if(this.currentTemp + this.damage - this.stableOutput >= this.maxTemp) {
				this.footerLabels.get(5).setText(this.warnings.get(2));
			} else if(this.damage - this.stableOutput > 0) {
				this.footerLabels.get(5).setText(this.warnings.get(1));
			} else {
				this.footerLabels.get(5).setText(this.warnings.get(0));
			}
			//set edit and add button status
			if(this.editRadioButton.isSelected()) {
				this.undoEditButton.setEnabled(true);
			} else {
				this.undoEditButton.setEnabled(this.tempLog.size() > 1);
			}
			this.lockThreads = false;
		}
	}
	
	//finds the mass of selected ammo when calculating ballistic damage
	public double findAmmoMass() {
		for(Weapon w : weapons) {
			if(w.getName().equals(String.valueOf(this.comboBoxes.get(2).getSelectedItem()))) {
				return ((Ammunition) w).getMass();
			}
		}
		return 0d;
	}
	
	//finds the velocity of selected ammo when calculating ballistic damage
	public double findAmmoVelocity() {
		for(Weapon w : weapons) {
			if(w.getName().equals(String.valueOf(this.comboBoxes.get(2).getSelectedItem()))) {
				return ((Ammunition) w).getVelocity();
			}
		}
		return 0d;
	}
	
	public void undoEdit() {
		if(!this.lockThreads) {
			this.lockThreads = true;
			//check to see the current state of the button
			if(this.selectRadioButton.isSelected()) {
				//undo last attack
				this.tempLog.remove(this.tempLog.size() - 1);
				this.chargeLog.remove(this.chargeLog.size() - 1);
				this.currentTemp = this.tempLog.get(this.tempLog.size() - 1);
				this.charge = this.chargeLog.get(this.chargeLog.size() - 1);
				if(this.tempLog.size() == 1) {
					this.undoEditButton.setEnabled(false);
				}
				updateHeader();
			} else {
				//EDIT WEAPON
				//read boxes and construct the appropriate weapon string
				String tempWeaponString = readBoxes();
				//read in weapon profiles file
				ArrayList<String> lines = new ArrayList<String>();
				try(BufferedReader br = new BufferedReader(new FileReader(weaponProfiles))) {
					String line = null;
					while((line = br.readLine()) != null) {
						lines.add(line.trim());
					}
				} catch(IOException e) {
					try(PrintStream output = new PrintStream("CrashLog.txt")) {
						e.printStackTrace(output);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
				//locate weapon to modify
				for(int i = 0; i < lines.size(); i++) {
					//isolate the type and name of the weapon and find a matching line
					//only check lines of the correct type
					String tempType = lines.get(i).substring(0, lines.get(i).indexOf(":::"));
					if(tempType.equals(String.valueOf(this.comboBoxes.get(0).getSelectedItem()))) {
						//find the line with the selected weapon name
						String tempName = lines.get(i).substring(lines.get(i).indexOf(":::") + 3);
						tempName = tempName.substring(0, tempName.indexOf(":::"));
						if(tempName.equals(String.valueOf(this.comboBoxes.get(1).getSelectedItem()))) {
							//replace substring with text from textbox
							//set weapon type (NOT EDITABLE)
							String outputLine = String.valueOf(this.comboBoxes.get(0).getSelectedItem()) + ":::";
							String textToProcess = lines.get(i).substring(lines.get(i).indexOf(":::") + 3);
							for(int j = 0; j < this.inputBoxes.size(); j++) {
								if(this.inputBoxes.get(j).isVisible()) {
									//only replace original substring with text if there is actually text to replace it with
									String firstPart = null;
									if(textToProcess.indexOf(":::") != -1) {
										firstPart = textToProcess.substring(0, textToProcess.indexOf(":::"));
									} else if(textToProcess.indexOf(":::") == -1) {
										firstPart = textToProcess;
									}
									textToProcess = textToProcess.substring(textToProcess.indexOf(":::") + 3);
									if(this.inputBoxes.get(j).getText().trim().length() > 0) {
										//replace the first instance of the combobox text with the textfield text in firstPart
										int location = firstPart.indexOf(String.valueOf(this.comboBoxes.get(j + 1).getSelectedItem()));
										int length = String.valueOf(this.comboBoxes.get(j + 1).getSelectedItem()).length();
										String tempPart = firstPart.substring(0, location);
										tempPart += this.inputBoxes.get(j).getText();
										tempPart += firstPart.substring(location + length);
										firstPart = tempPart;
									}
									outputLine += firstPart + ":::";
								}
							}
							lines.set(i, outputLine);
							break;
						}
					}
				}
				//write changes to text file
				try(PrintWriter output = new PrintWriter(weaponProfiles)) {
					for(String s : lines) {
						output.println(s);
					}
				} catch(FileNotFoundException e) {
					try(PrintStream output = new PrintStream("CrashLog.txt")) {
						e.printStackTrace(output);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			this.lockThreads = false;
		}
		//update the weapon list if an edit has been made
		if(!this.selectRadioButton.isSelected()) {
			weapons.clear();
			for(int i = 0; i < this.inputBoxes.size(); i++) {
				this.inputBoxes.get(i).setText("");
			}
			updateWeaponList();
		}
	}
	
	public void applyAdd() {
		if(!this.lockThreads) {
			this.lockThreads = true;
			//check to see the current state of the button
			if(this.selectRadioButton.isSelected()) {
				//APPLY ATTACK
				//update charge remaining
				this.charge -= this.damage;
				//update core temperature
				this.currentTemp += this.damage - this.stableOutput;
				if(this.currentTemp < 0) {
					this.currentTemp = 0;
				}
				//log the change to be used later if it needs to be undone
				this.tempLog.add(this.currentTemp);
				this.chargeLog.add(this.charge);
				if(!this.undoEditButton.isEnabled()) {
					this.undoEditButton.setEnabled(true);
				}
				updateHeader();
			} else {
				//ADD WEAPON
				//read boxes and construct the appropriate weapon string
				String tempWeaponString = readBoxes();
				try(PrintWriter output = new PrintWriter(weaponProfiles)) {
					output.append(tempWeaponString);
				} catch(FileNotFoundException e) {
					try(PrintStream output = new PrintStream("CrashLog.txt")) {
						e.printStackTrace(output);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			this.lockThreads = false;
		}
		//update the weapon list if an addition has been made
		if(!this.selectRadioButton.isSelected()) {
			updateWeaponList();
		}
	}
	
	public String readBoxes() {
		String weaponString = String.valueOf(this.comboBoxes.get(0).getSelectedItem()) + ":::";
		for(int i = 0; i < this.inputBoxes.size(); i++) {
			if(this.inputBoxes.get(i).isVisible()) { //only process visible boxes
				String temp = this.inputBoxes.get(i).getText().trim();
				//get the appropriate string
				if(temp.length() == 0) {
					//something has been entered into the text box
					temp = String.valueOf(this.comboBoxes.get(i + 1).getSelectedItem());
				}
				weaponString += temp + ":::";
			}
		}
		weaponString = weaponString.substring(0, weaponString.length() - 3);
		return weaponString;
	}
	
	public void updateWeaponList() {
		getFileContents(); //read weapons from file, sort weapons, add to arraylist, condense, and re-export weapons
		refreshBody(0); //set combo boxes with updated weapons
		updateFooter();
	}
	
	public void updateHeader() {
		this.headerLabels.get(1).setText(String.format("%.0f J", this.charge));
		this.headerLabels.get(3).setText(String.format("%.1f", this.currentTemp * 100 / this.maxTemp) + " %");
		//update max stable active time
		double seconds = this.charge / this.stableOutput;
		double minutes = seconds / 60d;
		int timeMinutes = (int) (minutes);
		seconds -= timeMinutes * 60;
		this.headerLabels.get(5).setText(String.valueOf(timeMinutes + " minutes, " + ((int) (seconds)) + " seconds"));
		//update estimated active time
		double estTime = this.charge / this.damage / 60d / 60d / 24d; //time is now in days
		String estimatedTimeString = "";
		boolean needComma = false;
		if(estTime >= 1) {
			int temp = (int) estTime;
			estTime -= temp;
			estimatedTimeString += String.valueOf(temp) + " d";
			needComma = true;
		}
		estTime *= 24d; //time is now in hours
		if(estTime >= 1) {
			int temp = (int) estTime;
			estTime -= temp;
			if(needComma) {
				estimatedTimeString += ", ";
			}
			estimatedTimeString += String.valueOf(temp) + " h";
			needComma = true;
		}
		estTime *= 60d; //time is now in minutes
		if(estTime >= 1) {
			int temp = (int) estTime;
			estTime -= temp;
			if(needComma) {
				estimatedTimeString += ", ";
			}
			estimatedTimeString += String.valueOf(temp) + " m";
			needComma = true;
		}
		estTime *= 60d; //time is now in seconds
		if(estTime >= 1) {
			if(needComma) {
				estimatedTimeString += ", ";
			}
			estimatedTimeString += String.valueOf((int) estTime) + " s";
		}
		this.headerLabels.get(7).setText(estimatedTimeString);
	}
	
	@SuppressWarnings("unchecked")
    public void refreshBody(int index) {
		if(!this.lockThreads) {
			this.lockThreads = true;
			String typeSelected = String.valueOf(comboBoxes.get(0).getSelectedItem());
			Weapon weaponSelected = null;
			ArrayList<String> tempList = new ArrayList<String>();
			switch(index) {
				case 0: //box 1 changed
					//set labels and comboBoxes according to selected weapon type
					switch(typeSelected) {
						case "Ballistic":
							this.bodyLabels.get(0).setText("Type: ");
							this.bodyLabels.get(1).setText("Name: ");
							this.bodyLabels.get(2).setText("Ammo: ");
							this.bodyLabels.get(3).setText("Muzzle Velocity (m/s): ");
							this.bodyLabels.get(4).setText("Rate of Fire (rps): ");
							this.bodyLabels.get(0).setVisible(true);
							this.bodyLabels.get(1).setVisible(true);
							this.bodyLabels.get(2).setVisible(true);
							this.bodyLabels.get(3).setVisible(true);
							this.bodyLabels.get(4).setVisible(true);
							this.comboBoxes.get(0).setVisible(true);
							this.comboBoxes.get(1).setVisible(true);
							this.comboBoxes.get(2).setVisible(true);
							this.comboBoxes.get(3).setVisible(true);
							this.comboBoxes.get(4).setVisible(true);
							for(int i = 0; i < this.inputBoxes.size(); i++) {
								this.inputBoxes.get(i).setVisible(this.editRadioButton.isSelected());
							}
							for(int i = 1; i < this.subInputBoxes.size(); i++) {
								this.subInputBoxes.get(i).setVisible(this.selectRadioButton.isSelected());
							}
							this.subInputBoxes.get(0).setVisible(false);
							this.durationRadioButton.setVisible(this.selectRadioButton.isSelected());
							this.numberOfShotsRadioButton.setVisible(this.selectRadioButton.isSelected());
						break;
						case "Explosive":
							this.bodyLabels.get(0).setText("Type: ");
							this.bodyLabels.get(1).setText("Name: ");
							this.bodyLabels.get(2).setText("Yield: ");
							this.bodyLabels.get(3).setText("Blast Shape: ");
							this.bodyLabels.get(4).setText("Distance (m): ");
							this.bodyLabels.get(0).setVisible(true);
							this.bodyLabels.get(1).setVisible(true);
							this.bodyLabels.get(2).setVisible(true);
							this.bodyLabels.get(3).setVisible(true);
							this.bodyLabels.get(4).setVisible(true);
							this.comboBoxes.get(0).setVisible(true);
							this.comboBoxes.get(1).setVisible(true);
							this.comboBoxes.get(2).setVisible(true);
							this.comboBoxes.get(3).setVisible(true);
							this.comboBoxes.get(4).setVisible(false);
							this.subInputBoxes.get(0).setVisible(true);
							if(this.editRadioButton.isSelected()) {
								for(int i = 0; i < this.inputBoxes.size(); i++) {
									this.inputBoxes.get(i).setVisible(true);
								}
								this.inputBoxes.get(3).setVisible(false);
								this.bodyLabels.get(4).setVisible(false);
								this.subInputBoxes.get(0).setVisible(false);
							}
							for(int i = 1; i < this.subInputBoxes.size(); i++) {
								this.subInputBoxes.get(i).setVisible(false);
							}
							this.durationRadioButton.setVisible(false);
							this.numberOfShotsRadioButton.setVisible(false);
						break;
						case "Melee":
							this.bodyLabels.get(0).setText("Type: ");
							this.bodyLabels.get(1).setText("Name: ");
							this.bodyLabels.get(2).setText("Mass: ");
							this.bodyLabels.get(3).setText("Speed: ");
							this.bodyLabels.get(0).setVisible(true);
							this.bodyLabels.get(1).setVisible(true);
							this.bodyLabels.get(2).setVisible(true);
							this.bodyLabels.get(3).setVisible(true);
							this.bodyLabels.get(4).setVisible(false);
							this.comboBoxes.get(0).setVisible(true);
							this.comboBoxes.get(1).setVisible(true);
							this.comboBoxes.get(2).setVisible(true);
							this.comboBoxes.get(3).setVisible(true);
							this.comboBoxes.get(4).setVisible(false);
							for(int i = 0; i < this.subInputBoxes.size(); i++) {
								this.subInputBoxes.get(i).setVisible(false);
							}
							if(this.editRadioButton.isSelected()) {
								for(int i = 0; i < 3; i++) {
									this.inputBoxes.get(i).setVisible(true);
								}
								this.inputBoxes.get(3).setVisible(false);
							}
							this.durationRadioButton.setVisible(false);
							this.numberOfShotsRadioButton.setVisible(false);
						break;
						case "Other":
							this.bodyLabels.get(0).setText("Type: ");
							this.bodyLabels.get(1).setText("Name: ");
							this.bodyLabels.get(2).setText("Output: ");
							this.bodyLabels.get(0).setVisible(true);
							this.bodyLabels.get(1).setVisible(true);
							this.bodyLabels.get(2).setVisible(true);
							this.bodyLabels.get(3).setVisible(false);
							this.bodyLabels.get(4).setVisible(false);
							this.comboBoxes.get(0).setVisible(true);
							this.comboBoxes.get(1).setVisible(true);
							this.comboBoxes.get(2).setVisible(true);
							this.comboBoxes.get(3).setVisible(false);
							this.comboBoxes.get(4).setVisible(false);
							for(int i = 0; i < this.subInputBoxes.size(); i++) {
								this.subInputBoxes.get(i).setVisible(false);
							}
							if(this.editRadioButton.isSelected()) {
								for(int i = 0; i < 2; i++) {
									this.inputBoxes.get(i).setVisible(true);
								}
								this.inputBoxes.get(2).setVisible(false);
								this.inputBoxes.get(3).setVisible(false);
							}
							this.durationRadioButton.setVisible(false);
							this.numberOfShotsRadioButton.setVisible(false);
						break;
						case "Ammunition":
							this.bodyLabels.get(0).setText("Type: ");
							this.bodyLabels.get(1).setText("Name: ");
							this.bodyLabels.get(2).setText("Mass: ");
							this.bodyLabels.get(3).setText("Velocity: ");
							this.bodyLabels.get(0).setVisible(true);
							this.bodyLabels.get(1).setVisible(true);
							this.bodyLabels.get(2).setVisible(true);
							this.bodyLabels.get(3).setVisible(true);
							this.bodyLabels.get(4).setVisible(false);
							this.comboBoxes.get(0).setVisible(true);
							this.comboBoxes.get(1).setVisible(true);
							this.comboBoxes.get(2).setVisible(true);
							this.comboBoxes.get(3).setVisible(true);
							this.comboBoxes.get(4).setVisible(false);
							for(int i = 0; i < this.subInputBoxes.size(); i++) {
								this.subInputBoxes.get(i).setVisible(false);
							}
							if(this.editRadioButton.isSelected()) {
								for(int i = 0; i < 3; i++) {
									this.inputBoxes.get(i).setVisible(true);
								}
								this.inputBoxes.get(3).setVisible(false);
							}
							this.durationRadioButton.setVisible(false);
							this.numberOfShotsRadioButton.setVisible(false);
						break;
					}
					//add items of matching type to box 2
					this.comboBoxes.get(1).removeAllItems();
					for(int i = 0; i < this.weapons.size(); i++) {
						if(String.valueOf(this.comboBoxes.get(0).getSelectedItem()).equals(this.weapons.get(i).getType())) {
							this.comboBoxes.get(1).addItem(this.weapons.get(i).getName());
						}
					}
				case 1: //box 2 changed
					//find the weapon selected in box 2
					for(int i = 0; i < this.weapons.size(); i++) {
						if(String.valueOf(((String) this.comboBoxes.get(1).getSelectedItem())).equals(this.weapons.get(i).getName())) {
							weaponSelected = this.weapons.get(i);
							break;
						}
					}
					//load all information for weaponSelected
					switch(typeSelected) {
						case "Ballistic":
							//load ammo types
							this.comboBoxes.get(2).removeAllItems();
							for(String s : ((Ballistic) weaponSelected).getAmmoTypes()) {
								this.comboBoxes.get(2).addItem(s);
							}
							//load muzzle velocity
							this.comboBoxes.get(3).removeAllItems();
							for(Double d : ((Ballistic) weaponSelected).getMuzzleVelocities()) {
								this.comboBoxes.get(3).addItem(d);
							}
							//load rate of fire
							this.comboBoxes.get(4).removeAllItems();
							for(Double d : ((Ballistic) weaponSelected).getRatesOfFire()) {
								this.comboBoxes.get(4).addItem(d);
							}
						break;
						case "Explosive":
							//load yields
							this.comboBoxes.get(2).removeAllItems();
							for(Double d : ((Explosive) weaponSelected).getYields()) {
								this.comboBoxes.get(2).addItem(d);
							}
							//load shaped
							this.comboBoxes.get(3).removeAllItems();
							for(String s : ((Explosive) weaponSelected).getShaped()) {
								this.comboBoxes.get(3).addItem(s);
							}
						break;
						case "Melee":
							//load masses
							this.comboBoxes.get(2).removeAllItems();
							for(Double d : ((Melee) weaponSelected).getMasses()) {
								this.comboBoxes.get(2).addItem(d);
							}
							//load speeds
							this.comboBoxes.get(3).removeAllItems();
							for(Double d : ((Melee) weaponSelected).getSpeeds()) {
								this.comboBoxes.get(3).addItem(d);
							}
						break;
						case "Other":
							//load outputs
							this.comboBoxes.get(2).removeAllItems();
							for(Double d : ((Other) weaponSelected).getOutputs()) {
								this.comboBoxes.get(2).addItem(d);
							}
						break;
						case "Ammunition":
							//load masses
							this.comboBoxes.get(2).removeAllItems();
							for(Double d : ((Ammunition) weaponSelected).getMasses()) {
								this.comboBoxes.get(2).addItem(d);
							}
							//load velocities
							this.comboBoxes.get(3).removeAllItems();
							for(Double d : ((Ammunition) weaponSelected).getVelocities()) {
								this.comboBoxes.get(3).addItem(d);
							}
						break;
					}
			}
			this.lockThreads = false;
			updateFooter();
		}
	}
	
	//Read in profiles for weapons and ammo
	public void getFileContents() {
		ArrayList<String> lines = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(this.weaponProfiles))) {
			String line = null;
			while((line = br.readLine()) != null) {
				lines.add(line.trim());
			}
			Collections.sort(lines);
			for(String s : lines) {
				this.weapons.add(WeaponFactory.make(s));
				//prevent invalid objects from WeaponProfiles.txt from being added to the program
				if(this.weapons.size() > 0) {
					if(this.weapons.get(this.weapons.size() - 1).getType().equals("INVALID")) {
						this.weapons.remove(weapons.size() - 1);
					}
				}
			}
			condenseWeapons();
		} catch(IOException e) {
			try(PrintStream output = new PrintStream("CrashLog.txt")) {
				e.printStackTrace(output);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		//output updated (sorted and condensed) database to WeaponProfiles.txt
		exportWeapons();
	}
	
	//output weapons to WeaponProfiles.txt
	public void exportWeapons() {
		try(PrintWriter output = new PrintWriter(this.weaponProfiles)) {
			for(Weapon w : this.weapons) {
				String outputString = w.getType() + ":::" + w.getName() + ":::";
				switch(w.getType()) {
					case "Ballistic":
						for(String s : ((Ballistic) w).getAmmoTypes()) {
							outputString += s + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3) + ":::";
						for(Double d : ((Ballistic) w).getMuzzleVelocities()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3) + ":::";
						for(Double d : ((Ballistic) w).getRatesOfFire()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3);
					break;
					case "Ammunition":
						for(Double d : ((Ammunition) w).getMasses()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3) + ":::";
						for(Double d : ((Ammunition) w).getVelocities()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3);
					break;
					case "Explosive":
						for(Double d : ((Explosive) w).getYields()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3) + ":::";
						for(String s : ((Explosive) w).getShaped()) {
							if(s.equals("Shaped")) {
								outputString += true + ",,,";
							} else {
								outputString += false + ",,,";
							}
						}
						outputString = outputString.substring(0,outputString.length() - 3);
					break;
					case "Melee":
						for(Double d : ((Melee) w).getMasses()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3) + ":::";
						for(Double d : ((Melee) w).getSpeeds()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3);
					break;
					case "Other":
						for(Double d : ((Other) w).getOutputs()) {
							outputString += d + ",,,";
						}
						outputString = outputString.substring(0,outputString.length() - 3);
					break;
				}
				output.println(outputString);
			}
		} catch(Exception e) {
			try(PrintWriter output = new PrintWriter("CrashLog.txt")) {
				e.printStackTrace(output);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	//take entries from the weapons array list and ensure that no duplicates remain; also takes subsets of specific weapons and puts them under the same weapon entry
	public void condenseWeapons() {
		for(int i = 0; i < this.weapons.size() - 1; i++) {
			for(int j = i + 1; j < this.weapons.size(); j++) {
				//if the weapon has the same type and name as another, condense them at the point of divergence
				if(this.weapons.get(i).getType().equals(this.weapons.get(j).getType()) && this.weapons.get(i).getName().equals(this.weapons.get(j).getName())) {
					switch(this.weapons.get(i).getType()) {
						case "Ballistic":
							//merge ammoTypes, muzzleVelocities, ratesOfFire
							((Ballistic) this.weapons.get(i)).setAmmoTypes(mergeStrings(((Ballistic) this.weapons.get(i)).getAmmoTypes(), ((Ballistic) this.weapons.get(j)).getAmmoTypes()));
							((Ballistic) this.weapons.get(i)).setRatesOfFire(mergeDoubles(((Ballistic) this.weapons.get(i)).getRatesOfFire(), ((Ballistic) this.weapons.get(j)).getRatesOfFire()));
							((Ballistic) this.weapons.get(i)).setMuzzleVelocities(mergeDoubles(((Ballistic) this.weapons.get(i)).getMuzzleVelocities(), ((Ballistic) this.weapons.get(j)).getMuzzleVelocities()));
							this.weapons.remove(j--);
						break;
						case "Explosive":
							((Explosive) this.weapons.get(i)).setYields(mergeDoubles(((Explosive) this.weapons.get(i)).getYields(), ((Explosive) this.weapons.get(j)).getYields()));
							((Explosive) this.weapons.get(i)).setShaped(mergeStrings(((Explosive) this.weapons.get(i)).getShaped(), ((Explosive) this.weapons.get(j)).getShaped()));
							this.weapons.remove(j--);
						break;
						case "Melee":
							((Melee) this.weapons.get(i)).setMasses(mergeDoubles(((Melee) this.weapons.get(i)).getMasses(), ((Melee) this.weapons.get(j)).getMasses()));
							((Melee) this.weapons.get(i)).setSpeeds(mergeDoubles(((Melee) this.weapons.get(i)).getSpeeds(), ((Melee) this.weapons.get(j)).getSpeeds()));
							this.weapons.remove(j--);
						break;
						case "Other":
							((Other) this.weapons.get(i)).setOutputs(mergeDoubles(((Other) this.weapons.get(i)).getOutputs(), ((Other) this.weapons.get(j)).getOutputs()));
							this.weapons.remove(j--);
						break;
						case "Ammunition":
							((Ammunition) this.weapons.get(i)).setMasses(mergeDoubles(((Ammunition) this.weapons.get(i)).getMasses(), ((Ammunition) this.weapons.get(j)).getMasses()));
							((Ammunition) this.weapons.get(i)).setVelocities(mergeDoubles(((Ammunition) this.weapons.get(i)).getVelocities(), ((Ammunition) this.weapons.get(j)).getVelocities()));
							this.weapons.remove(j--);
						break;
					}
				}
			}
		}
	}
	
	//merge two array lists and eliminate duplicates; return the resulting list
	public ArrayList<String> mergeStrings(ArrayList<String> listOne, ArrayList<String> listTwo) {
		ArrayList<String> sortedList = new ArrayList<String>();
		//merge all into one list
		for(String s : listOne) {
			sortedList.add(s);
		}
		for(String s : listTwo) {
			sortedList.add(s);
		}
		//sort alphabetically
		Collections.sort(sortedList);
		//check items next to each other to see if they are the same
		for(int i = 0; i < sortedList.size() - 1; i++) {
			for(int j = 1; j < sortedList.size(); j++) {
				if(sortedList.get(i).equals(sortedList.get(j))) {
					sortedList.remove(j--);
				} else {
					break;
				}
			}
		}
		return sortedList;
	}
	
	public ArrayList<Double> mergeDoubles(ArrayList<Double> listOne, ArrayList<Double> listTwo) {
		ArrayList<Double> sortedList = new ArrayList<Double>();
		//merge all into one list
		for(Double d : listOne) {
			sortedList.add(d);
		}
		for(Double d : listTwo) {
			sortedList.add(d);
		}
		//sort numerically
		Collections.sort(sortedList);
		//check items next to each other to see if they are the same
		for(int i = 0; i < sortedList.size() - 1; i++) {
			for(int j = 1; j < sortedList.size(); j++) {
				//this conversion from double to string is needed to account for fluxuations in 
				if(String.format("%.0f", sortedList.get(i)).equals(String.format("%.0f", sortedList.get(j)))) {
					sortedList.remove(j--);
				} else {
					break;
				}
			}
		}
		return sortedList;
	}
	
	//if edit is selected, value = true; if select is selected, value = false
	public void toggleInputBoxes(boolean value) {
		if(!this.lockThreads) {
			this.lockThreads = true;
			for(int i = 0; i < this.inputBoxes.size(); i++) {
				this.inputBoxes.get(i).setVisible(value);
			}
			switch(String.valueOf(this.comboBoxes.get(0).getSelectedItem())) {
				case "Other":
					this.inputBoxes.get(2).setVisible(false);
				case "Explosive":
				case "Melee":
				case "Ammunition":
					this.inputBoxes.get(3).setVisible(false);
				break;
			}
			switch(String.valueOf(this.comboBoxes.get(0).getSelectedItem())) {
				case "Ballistic":
					for(int i = 1; i < this.subInputBoxes.size(); i++) {
						this.subInputBoxes.get(i).setVisible(!value);
					}
					this.durationRadioButton.setVisible(!value);
					this.numberOfShotsRadioButton.setVisible(!value);
				break;
				case "Explosive":
					this.subInputBoxes.get(0).setVisible(!value);
				break;
			}
			if(String.valueOf(this.comboBoxes.get(0).getSelectedItem()).equals("Explosive")) {
				this.bodyLabels.get(4).setVisible(!value);
			}
			if(value) {
				this.undoEditButton.setText("EDIT WEAPON");
				this.applyAddButton.setText("ADD WEAPON");
			} else {
				this.undoEditButton.setText("UNDO DAMAGE");
				this.applyAddButton.setText("APPLY DAMAGE");
			}
			this.lockThreads = false;
			updateFooter();
		}
	}
	
	public static void main(String args[]) {
		//Create and display the form
		Calculator gui = new Calculator();
		gui.setVisible(true);
    }
}