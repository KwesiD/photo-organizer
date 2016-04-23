import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Date;

import com.drew.imaging.*;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;


/**
 * Contains the organizer constructor as well as all necessary methods. 
 * Used to organize files into folders based on their dates. Utilizes 
 * the metadata extractor library from https://drewnoakes.com/code/exif/ 
 * @author Kwesi Daniel
 *
 */
public class Organizer {

	//private File folder = null; //unused 
	private File dst = null;  //stores destination 
	private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"}; //months of the year. Numerical 
																										//value is index+1
	
	/**
	 * Creates Organizer object.
	 * @param folder
	 * @param dst
	 * @throws Exception
	 */
	Organizer(File folder,File dst) throws Exception{
		if(!folder.exists()){throw new FileNotFoundException("Folder not found!");} //Checks for common exceptions
		if(!folder.canRead()){throw new SecurityException("Cannot read folder!");}
		if(!folder.isDirectory()){throw new IllegalArgumentException("Invalid folder!");}
		//this.folder = folder;
		this.dst = dst;
	}
	
	/**
	 * Gets all the files in every sub folder from the given 'super'folder
	 * recursively and sorts them.
	 * @param folder
	 * @throws Exception
	 */
	public void getFiles(File folder) throws Exception{
		File[] files = folder.listFiles();
		for(int i = 0;i < files.length;i++){
			if(files[i].isDirectory()){getFiles(files[i]);}
			if(isImage(files[i])){organize(files[i]);} //checks if image
			else if(isVideo(files[i])){organize(files[i]);} //checks if video
		}
	}
	
	/**
	 * Organizes files into the proper folder based on taken/last modified date.
	 * @param file
	 * @throws IOException
	 * @throws ImageProcessingException
	 */
	private void organize(File file) throws IOException, ImageProcessingException {
		
		BasicFileAttributes attributes =  Files.readAttributes(file.toPath(), BasicFileAttributes.class, LinkOption.values());
		String lastModifiedDate = attributes.lastModifiedTime().toString(); //Gets the last modified date
		if(isVideo(file)){move(file,dst,parseLastModified(lastModifiedDate)); return;} //Some video files are not supported by com.drew.* library
		Metadata metadata = null;
		try{metadata = ImageMetadataReader.readMetadata(file);} 
		catch(ImageProcessingException ipe){ipe.printStackTrace(); return;} //If file is not supported (ie not an image. Videos are processed above)
		ArrayList<Directory> directory = null;
		try{directory = (ArrayList<Directory>) metadata.getDirectories();}
		catch(NullPointerException npe){npe.printStackTrace(); return;}
		String creationDate = "";
		for(int i = 0;i < directory.size();i++){
			Date date = directory.get(i).getDate(ExifDirectoryBase.TAG_DATETIME); //Gets the creation date
			if(date != null){
				creationDate = date.toString();
			}
		}
	move(file,dst,compare(lastModifiedDate,creationDate)); //Moves file to proper folder
		
	}
	
	/**
	 * Creates proper folders for each date and moves files into them.	
	 * @param file
	 * @param dst
	 * @param date
	 * @throws IOException
	 */
	private void move(File file,File dst, PhotoDate date) throws IOException{
		File yearFolder = new File(dst.getPath()+ "\\" + date.getYear()); //Folder for the year 
		File monthFolder = new File(yearFolder.getPath()+ "\\" + date.getMonthName()); //Folder for the month
		File dayFolder = new File(monthFolder.getPath()+ "\\" + date.getMonth()+ "-" + date.getDay()); //Folder for the day
		if(!dayFolder.exists()){dayFolder.mkdirs();} //mkdirs makes all nonexistent but necessary parent folders. As opposed to mkdir(no s)
		File dstPath = new File(dayFolder.getPath()+ "\\" + file.getName());
		Files.move(file.toPath(),dstPath.toPath(),StandardCopyOption.REPLACE_EXISTING); //Copies folder over. Deletes original
		
		
	}
	
	/**
	 * Compares last modified date to creation date. Whichever is earliest gets used.
	 * Checks year, then month, then day
	 * @param lastModified
	 * @param creationDate
	 * @return
	 */
	private PhotoDate compare(String lastModified,String creationDate){
		PhotoDate lm = parseLastModified(lastModified);
		if(creationDate == ""){return lm;}
		PhotoDate cd = parseCreationDate(creationDate);
		if(lm.toString().equals(cd.toString())){return lm;} //lm = cd
		if(lm.getYear().compareTo(cd.getYear()) < 0){return lm;}   //We are trying to return the earliest date, so the signs 
		else if(lm.getYear().compareTo(cd.getYear()) > 0){return cd;} //are flipped around.
		else{ 
			if(lm.getMonth().compareTo(cd.getMonth()) < 0){return lm;} 
			else if(lm.getMonth().compareTo(cd.getMonth()) > 0){return cd;} 
			else{
				if(lm.getDay().compareTo(cd.getDay()) < 0){return lm;} 
				else {return cd;} 
			}
		}
	}
	
	/**
	 * Gets the year, month and day of last modified date.
	 * Last modified dates are all numerical on windows.
	 * @param s
	 * @return
	 */
	private PhotoDate parseLastModified(String s){
		String year = s.substring(0,4); 
		String day = s.substring(8,10);
		String month = s.substring(5,7);
		PhotoDate date = new PhotoDate(month,day,year); 
		return date;
	}
	
	/**
	 * Gets the year, month and day of the creation date.
	 * @param s
	 * @return
	 */
	private PhotoDate parseCreationDate(String s){
		String year = s.substring(s.length()-4,s.length());
		String day = s.substring(8,10);
		String month = s.substring(4,7);
		for(int i = 0;i < months.length;i++){ //Gets the numerical equivalent of the month
			if(months[i].equals(month)){
				if(i >= 9){ // index 9 = month # 10
					month = "" + (i+1);
					break;
				}
				month = "0" + (i+1);
				break;
			}
		}
		PhotoDate date = new PhotoDate(month,day,year);
		return date;
	}
	
	/**
	 * Checks if file has a video extension. More formats can be added at
	 * any time. 
	 * @param file
	 * @return
	 */
	private boolean isVideo(File file){
		String filename = file.getName().toLowerCase();
		if(
				filename.endsWith(".avi") ||
				filename.endsWith(".mov") ||  filename.endsWith(".flv") || 
				filename.endsWith(".wmv") ||  filename.endsWith(".mp4") || 
				filename.endsWith(".mpg") ||  filename.endsWith(".mpeg")
				){return true;}
		
		return false;
			
	}
	/**
	 * Checks if file has an image extension. More formats can be
	 * added at any time.
	 * @param file
	 * @return
	 */
	private boolean isImage(File file){
		String filename = file.getName().toLowerCase();
		if(
				filename.endsWith(".jpg") ||  filename.endsWith(".jpeg")||
				filename.endsWith(".gif") ||  filename.endsWith(".png") ||
				filename.endsWith(".bmp")){ return true;}
		
		return false;
			
	}
}
