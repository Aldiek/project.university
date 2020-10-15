package Hidden;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

public class steg1 {
   
    public static boolean encode(String path, String message) throws IOException
	{
		BufferedImage 	image	= Steganography.getImage(path);
		//user space is not necessary for Encrypting
		image = add_text(image,message);
		String path1=path.substring(0,path.length()-4)+"1"+path.substring(path.length()-4, path.length());
		return(Steganography.setImage(image,new File(path1),"png"));
	}
    /*
	 *Decrypt assumes the image being used is of type .png, extracts the hidden text from an image
	 *@param path   The path (folder) containing the image to extract the message from
	 *@param name The name of the image to extract the message from
	 *@param type integer representing either basic or advanced encoding
	 */
	public static String decode(String path)
	{
		byte[] decode;
		try
		{
			//user space is necessary for decrypting
                    		BufferedImage 	image	= Steganography.getImage(path);
			decode = decode_text(Steganography.get_byte_data(image),image);
			return(new String(decode));
		}
        catch(Exception e)
        {
			JOptionPane.showMessageDialog(null, 
				"لايوجد رسالة مخفية!","خطأ",
				JOptionPane.ERROR_MESSAGE);
			return "";
        }
    }
        /*
	 *Handles the addition of text into an image
	 *@param image The image to add hidden text to
	 *@param text     The text to hide in the image
	 *@return Returns the image with the text embedded in it
	 */
        ////////////////----------------------/////////finished
	private static BufferedImage add_text(BufferedImage image, String text)
	{   // System.out.println(image.getWidth());
                //System.out.println(image.getHeight());
		//convert all items to byte arrays: image, message, message length
		byte img[]  = Steganography.get_byte_data(image);
		byte msg[] = text.getBytes();
		byte len[]   = Steganography.bit_conversion(msg.length);
//		try
//		{
			encode_text(img,image, len,  0); //0 first positiong
			encode_text(img,image, msg, 4); //4 bytes of space for length: 4bytes*8bit = 32 bits
//		}
//		catch(Exception e)
//		{System.err.println(e);
//			JOptionPane.showMessageDialog(null, 
//"Target File cannot hold message!", "Error",JOptionPane.ERROR_MESSAGE);
//		}
		return image;
	}

	/*
	 *Encode an array of bytes into another array of bytes at a supplied offset
	 *@param image     Array of data representing an image
	 *@param addition Array of data to add to the supplied image data array
	 *@param offset      The offset into the image array to add the addition data
	 *@return Returns data Array of merged image and addition data
	 */
        ////////////--------///
	private static byte[] encode_text(byte[] image,BufferedImage image1, byte[] addition, int offset)
	{//System.out.println(image.length);
		//check that the data + offset will fit in the image
		if(addition.length + offset > image.length)
		{
			throw new IllegalArgumentException("الملف صغير!");
		}
		//loop through each addition byte
                int m=image1.getHeight();
               // System.out.println("sss"+m);
                int n=image1.getWidth()*3;
             //                   System.out.println("sss"+n);
           // System.out.println(image.length);
                byte[][]im=new byte[m][n];
                int k=0;
                for(int i=0;i<m;i++){
                for(int j=0;j<n;j++)
                {im[i][j]=image[k];
                k++;}
                }
                int i=0;
                 int r=0;                
                int c=offset;
                int len=addition.length;
                while(r<len){
                    ////////السطر الاول
                for(int j=c;j<n-i;j++){
                 
               if(r>=len)im[i][j]=(byte)122;
               else{int add=addition[r++];
               im[i][j]=(byte)add;}
                }
                 //السطر الاخير
                for(int j=i;j<n-i;j++){
                  
                 if(r>=len)im[m-1-i][j]=(byte)122;
                 else{  int add=addition[r++];
                    im[m-1-i][j]=(byte)add;}
                }
    //العمود الاول
                for(int j=i+1;j<m-i-1;j++){
                    
                if(r>=len)im[j][i]=(byte)122;
                else{int add=addition[r++];
                    im[j][i]=(byte)add;}
                }
               
                //العمودالاخير
                 for(int j=i+1;j<m-i-1;j++){
                   
         if(r>=len)im[j][n-1-i]=(byte)122;
         else{ int add=addition[r++];
                    im[j][n-1-i]=(byte)add;}
                }
                
                i++;
                c=i;
                }////while
                

k=0;
for(int x=0;x<m;x++){
                for(int j=0;j<n;j++)
                {image[k]=im[x][j];
                k++;}
                }

		return image;
	}
    
	/*
	 *Retrieves hidden text from an image
	 *@param image Array of data, representing an image
	 *@return Array of data which contains the hidden text
	 */
	private static byte[] decode_text(byte[] image,BufferedImage image1)
	{
		int len = 0;
		int offset  = 4;
		//loop through 32 bytes of data to determine text length
		for(int i=0; i<4; ++i) //i=24 will also work, as only the 4th byte contains real data
		{
			len = (len << 8) | (image[i] &0xff);
		}
		
		byte[] result = new byte[len];
		
                int m=image1.getHeight();
               
                int n=image1.getWidth()*3;
                byte[][]im=new byte[m][n];
                int k=0;
                for(int i=0;i<m;i++){
                for(int j=0;j<n;j++)
                {im[i][j]=image[k];
                k++;}
                }
                int i=0;
                 int r=0;
                
                int c=offset;
                while(r<len){
                    ////////السطر الاول
                for(int j=c;j<n-i;j++,r++){
               if(r<len)
               result[r]=im[i][j];
                }
                 //السطر الاخير
                for(int j=i;j<n-i;j++,r++){
             if(r<len)
                    result[r]=im[m-1-i][j];
                }
    //العمود الاول
                for(int j=i+1;j<m-i-1;j++,r++){
               if(r<len)
                    result[r]=im[j][i];
                }
               
                //العمودالاخير
                 for(int j=i+1;j<m-i-1;j++,r++){
                     if(r<len)
               result[r]=im[j][n-1-i];
                 }
                
                i++;
                c=i;
                }////while
                
           
		return result;
	}
        
        
        public static void main(String[] args) {
    }
}

