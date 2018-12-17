import javax.swing.*;                   // Used for GUI
import java.awt.*;                     // for gridlayout and fancy colors
import java.io.*;                     // Used for standard IO 
import java.awt.event.*;             // For some reason just importing java.awt.* doesn't get the action listner stuff so this needs to be here for that
import java.awt.image.BufferedImage;// Same as above but for bufferedimages
import javax.imageio.ImageIO;      // image manip

public class GridLayoutAuto extends JFrame implements ActionListener
{
   // UI Labels, Buttons and File Browser
   private JLabel instructionsText = new JLabel("SELECT FILE TO TURN INTO ASCII ART: PNG, JPG or JPEG (Excuse the Bad UI)");  
   private JFileChooser fileBrowser = new JFileChooser();
   private JButton generateButton = new JButton("Click to generate Ascii Art");
   private JButton openFiles = new JButton("Click to Choose an image");
   
   // Images / File Variables
   private File imageFile = null; 
   private Image picture = null;
   
   // Screensize
   public static final int x = 500; // MAKE GENERIC / BASED ON SCREEN SIZE later
   public static final int y = 200;
   
   // Constructor For GUI (Builds GUI and adds action listeners)
   public GridLayoutAuto()
   {
      super();
      
      setSize(x,y);
      setLayout(new GridLayout(3,1));
      setTitle("ASCII GENERATOR"); 
      setIconImage((new ImageIcon("icon.png")).getImage());
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      generateButton.addActionListener(this);
      openFiles.addActionListener(this);
      
      add(instructionsText);
      add(openFiles);
      add(generateButton);
      
   }
   
   // Actions Listeners for button presses
   public void actionPerformed(ActionEvent buttonClicked)
   {     
      // If Generate Ascii Art is clicked (and a picture file exists)
      if ( ((buttonClicked.getActionCommand()).equals("Click to generate Ascii Art")) && (picture != null) ) 
      {
         // Converts Image object to BufferedImage object
         BufferedImage bufImg = new BufferedImage(picture.getWidth(null), picture.getHeight(null), BufferedImage.TYPE_INT_ARGB);
         Graphics2D graphics = bufImg.createGraphics();
         graphics.drawImage(picture, 0, 0, null);
         graphics.dispose();
         
         // Creates Ascii Text file and shows process is completed
         generateAscii(bufImg, imageFile.getName());
         instructionsText.setText("Finished generating file... File Name: " + imageFile.getName() + ".txt"); 
         instructionsText.setForeground(Color.GREEN);
      }
      // If Generate Ascii is clicked (and picture file has not been created)
      else if ( ((buttonClicked.getActionCommand()).equals("Click to generate Ascii Art")) && (picture == null) )
      {
         // Displays error
         instructionsText.setText("ERRROR: Select a file first! (Click the other button!)");
         instructionsText.setForeground(Color.RED);
      }
      // If file chooser is chosen
      else
      {
         // Opens file browser and stores selected file
         int fileReturn = fileBrowser.showOpenDialog(null);
         if (fileReturn == JFileChooser.APPROVE_OPTION)
         {
            imageFile = fileBrowser.getSelectedFile();
            
            // If the File object is an image file (jpeg or png) scale the Image object and display filename 
            if (isImage(imageFile.getName()))
            {
               instructionsText.setText("File Selected: " + imageFile.getName());
               try
               {
                  picture = ImageIO.read(imageFile);
                  picture = picture.getScaledInstance(100, ((picture.getHeight(null) * 100) / picture.getWidth(null)), Image.SCALE_DEFAULT);
               } 
               catch (IOException ex) 
               {
                  instructionsText.setText("ERROR READING FILE");
                  instructionsText.setForeground(Color.RED);
                  picture = null;
                  imageFile = null;
               }
            }
            // If File object is not an image display error
            else
            {
               instructionsText.setText("File selected is not an image (jpeg, png or jpg)");
               imageFile = null;
            }
         }
      }
   }
   // Generates ascii .txt file from BufferedImage 
   public void generateAscii(BufferedImage pic, String name)
   {
      try 
      {
        Color rgbVals;
        int grayScale = 0;
        
        // Creates output file
        FileWriter writeChar = new FileWriter((name + ".txt"), true);
        
        // Loops through all pixels in scaled image and getting the grayscale RGB value for each pixel and writes the character related to that value
        for (int i = 0; i < pic.getHeight(null); i++)
        {
           for (int j = 0; j < pic.getWidth(null); j++)
           {
               rgbVals =  new Color(pic.getRGB(j, i));
               grayScale = (rgbVals.getRed() + rgbVals.getBlue() + rgbVals.getGreen()) / 3;
               writeChar.write(asciiChar(grayScale));
           }
           writeChar.write('\n');
        }
        writeChar.close();   
     }
     catch (IOException ex)
     {
        instructionsText.setText("ERROR: Problem creating ascii file");
        instructionsText.setForeground(Color.RED);
        return;
     }
     return;
   }
   // Returns character (# % : .  ) depending on how dark the grayscale pixel is
   // NOTE: Will increase the number of output chracters later :) 
   public char asciiChar(int avgVal)
   {
      if (avgVal < 51)
         return '#';
      else if ( (avgVal < 102) && (avgVal > 50) )
         return '%';
      else if ( (avgVal < 153) && (avgVal > 101) )
         return ':';
      else if ( (avgVal < 204) && (avgVal > 152) )
         return '.';
      else if ( (avgVal <= 255) && (avgVal > 203) )
         return ' ';
      else 
         return '!'; // used for debug 
   }
   
   // Determines if the File object is an image (based off of file extension) 
   public boolean isImage(String filename)
   {
      String[] splitter = filename.split("[.]");
      int fileExt = (splitter.length - 1);

      if ( ((splitter[fileExt]).equalsIgnoreCase("JPEG")) || ((splitter[fileExt]).equalsIgnoreCase("JPG")) || ((splitter[fileExt]).equalsIgnoreCase("PNG")) )
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}
