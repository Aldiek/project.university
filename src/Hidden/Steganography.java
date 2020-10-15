package Hidden;
import java.awt.Desktop;
import java.io.File;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Steganography
{
	public static boolean encode(String path, String message) throws IOException
	{
		BufferedImage 	image	= getImage(path);
		//user space is not necessary for Encrypting
		image = add_text(image,message);
		String path1=path.substring(0,path.length()-4)+"1"+path.substring(path.length()-4, path.length());
		return(setImage(image,new File(path1),"png"));
	}
	public static String decode(String path)
	{
		byte[] decode;
		try
		{
			//user space is necessary for decrypting
                    	BufferedImage 	image	= Steganography.getImage(path);
			//BufferedImage image  = user_space(getImage(path));
			decode = decode_text(get_byte_data(image));
			return(new String(decode));
		}
        catch(Exception e)
        {
			JOptionPane.showMessageDialog(null, 
				"لايوجد رسالة لاخفاؤها!","خطأ",
				JOptionPane.ERROR_MESSAGE);
			return "";
        }
    }
    
	public static BufferedImage getImage(String f)
	{
		BufferedImage image= null;
		File file = new File(f);		
		try
		{
			image = ImageIO.read(file);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, 
				"النص المدخل كبير جرب الاخفاء عن طريق الاطار!","خطأ",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	/*
	 *Set method to save an image file
	 *@param image The image file to save
	 *@param file      File  to save the image to
	 *@param ext      The extension and thus format of the file to be saved
	 *@return Returns true if the save is succesful
	 */
	public static boolean setImage(BufferedImage image, File file, String ext)
	{
		try
		{
			file.delete(); //delete resources used by the File
			ImageIO.write(image,ext,file);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
				"لانستطيع حفظ الملف!","خطأ",JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/*
	 *Handles the addition of text into an image
	 *@param image The image to add hidden text to
	 *@param text     The text to hide in the image
	 *@return Returns the image with the text embedded in it
	 */
        ////////////////----------------------/////////
	private static BufferedImage add_text(BufferedImage image, String text)
	{    
		byte img[]  = get_byte_data(image);
		byte msg[] = text.getBytes();
		byte len[]   = bit_conversion(msg.length);
		try
		{
			encode_text(img, len,  0); //0 first positiong
			encode_text(img, msg, 32); //4 bytes of space for length: 4bytes*8bit = 32 bits
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
"النص كبير حاول اخفاؤو بخوارزمية الاطار!", "خطأ",JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	/*
	 *Creates a user space version of a Buffered Image, for editing and saving bytes
	 *@param image The image to put into user space, removes compression interferences
	 *@return The user space version of the supplied image
	 */
 
	/*
	 *Gets the byte array of an image
	 *@param image The image to get byte data from
	 *@return Returns the byte array of the image supplied
	 *@see Raster
	 *@see WritableRaster
	 *@see DataBufferByte
	 */
        ////--------;;;;;//////////
	public static byte[] get_byte_data(BufferedImage image)
	{
		WritableRaster raster   = image.getRaster();
		DataBufferByte buffer = (DataBufferByte)raster.getDataBuffer();
		return buffer.getData();
	}
	/*
	 *Gernerates proper byte format of an integer
	 *@param i The integer to convert
	 *@return Returns a byte[4] array converting the supplied integer into bytes
	 */
        ////////////-/-;;;;;/////
	public static byte[] bit_conversion(int i)
	{
		//only using 4 bytes
		byte byte3 = (byte)((i & 0xFF000000) >>> 24); //0
		byte byte2 = (byte)((i & 0x00FF0000) >>> 16); //0
		byte byte1 = (byte)((i & 0x0000FF00) >>> 8 ); //0
		byte byte0 = (byte)((i & 0x000000FF)       );
		return(new byte[]{byte3,byte2,byte1,byte0});
	}
	
	/*
	 *Encode an array of bytes into another array of bytes at a supplied offset
	 *@param image     Array of data representing an image
	 *@param addition Array of data to add to the supplied image data array
	 *@param offset      The offset into the image array to add the addition data
	 *@return Returns data Array of merged image and addition data
	 */
        ////////////--------///
	private static byte[] encode_text(byte[] image, byte[] addition, int offset)
	{
		//check that the data + offset will fit in the image
		if(addition.length + offset > image.length)
		{
			throw new IllegalArgumentException("الملف صغير!");
		}
		//loop through each addition byte
		for(int i=0; i<addition.length; ++i)
		{
			int add = addition[i];
			for(int bit=7; bit>=0; --bit, ++offset) //ensure the new offset value carries on through both loops
			{
				int b = (add >>> bit) & 1;
				image[offset] = (byte)((image[offset] & 0xFE) | b );
			}
		}
		return image;
	}
    
	/*
	 *Retrieves hidden text from an image
	 *@param image Array of data, representing an image
	 *@return Array of data which contains the hidden text
	 */
	private static byte[] decode_text(byte[] image)
	{
		int length = 0;
		int offset  = 32;
		//loop through 32 bytes of data to determine text length
		for(int i=0; i<32; ++i) //i=24 will also work, as only the 4th byte contains real data
		{
			length = (length << 1) | (image[i] & 1);
		}
		
		byte[] result = new byte[length];
		
		//loop through each byte of text
		for(int b=0; b<result.length; ++b )
		{
			//loop through each bit within a byte of text
			for(int i=0; i<8; ++i, ++offset)
			{
				//assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
				result[b] = (byte)((result[b] << 1) | (image[offset] & 1));
			}
		}
		return result;
	}
        
        public static void main(String[] args) {
            
    }
}

