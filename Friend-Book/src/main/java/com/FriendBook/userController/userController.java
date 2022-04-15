package com.FriendBook.userController;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.FriendBook.userEntity.LikeCommentOnPost;
import com.FriendBook.userEntity.Request;
import com.FriendBook.userEntity.SaveUserPost;
import com.FriendBook.userEntity.userData;
import com.FriendBook.userService.LikeCommentService;
import com.FriendBook.userService.RequestService;
import com.FriendBook.userService.SaveUserPostService;
import com.FriendBook.userService.UserService;


@Controller

public class userController  {

	@Autowired
	UserService  userService;
	@Autowired
	RequestService requestservice;
	@Autowired
	SaveUserPostService saveUserPostService;
	@Autowired
	LikeCommentService likeCommentService;
	int fid;
	userData allUserDetail=new userData();
	java.util.ArrayList<userData> searchresult=new ArrayList<>();

int postid;
String dsc;
	
	
	public final String UPLOAD_DIR=new ClassPathResource("static/img").getFile().getAbsolutePath();
	
public userController() throws IOException {
		
	}

@RequestMapping("/test")
public ModelAndView test1(){
	
	    return new ModelAndView("fbHome");
}

@RequestMapping("/login")
public ModelAndView test2(){
	
	    return new ModelAndView("fbLogin");
}

@RequestMapping("/signup")
public ModelAndView test3(){
	
	    return new ModelAndView("fbSignup");
}

@RequestMapping("/userProfile")
public String test4(Model model){
	 
	 model.addAttribute("fbname",allUserDetail.getFbName());	
	 model.addAttribute("book",allUserDetail.getBook());
	 model.addAttribute("movie",allUserDetail.getMovie() );
	 model.addAttribute("place",allUserDetail.getPlace());
	 model.addAttribute("song",allUserDetail.getSong());
	 model.addAttribute("followers",allUserDetail.getFollowers());
	 model.addAttribute("following",allUserDetail.getFollowing());
	 model.addAttribute("dp",allUserDetail.getDp());
	 model.addAttribute("post",allUserDetail.getPost());
	    return "fbUserProfile";
}

@RequestMapping("/seeDetails")
public ModelAndView test5(){
	
	    return new ModelAndView("fbSeeDetails");
}

@RequestMapping("/post")
public ModelAndView test6(){
		    return new ModelAndView("fbPost");
}

@RequestMapping("/userHome")
public String test7(Model model){
	
//	22222222222222222222222222222
	
	ArrayList<Request> al=requestservice.howManyFriends(allUserDetail.getId(), true);
	System.out.println("i have these frnd  =  "+al);
	ArrayList<SaveUserPost> posts=new ArrayList<>();
	
	for(Request r:al) {
		ArrayList <SaveUserPost> sup=saveUserPostService.getFriendsPost(r.getReceiverId());
		for(SaveUserPost s : sup) {
		posts.add(s);
		}
	}
	
	System.out.println("my frndsssssssssssss  :   "+posts);
	//ArrayList <userData> frnddata=new ArrayList<>();
	//for(SaveUserPost r:posts) {
	//	userData ud=userService.friendRequestDetail(r.getUserid());
	//	frnddata.add(ud);
//	}

	//model.addAttribute("frndname",frnddata);
	 model.addAttribute("post",posts);
	
	 model.addAttribute("name" ,allUserDetail.getName());
	
	    return "fbUserHome";
}


@RequestMapping("/register")
public String register(@ModelAttribute userData ud){
 System.out.println(ud);
 
 String n=ud.getName();

 String un=Character.toString(n.charAt(0)).toUpperCase()+n.substring(1, 5)+Integer.toString((int)Math.floor(Math.random() * 1000));
  ud.setFbName(un);
  System.out.println(ud);
  userService.doregister(ud);
    return "fbLogin";
}



@RequestMapping("/loginverification")
public String login(@ModelAttribute userData ud,Model model){
	
	

	//Empty
	if(ud.getEmail().equals("") && ud.getPassword().equals("")) {
	 return "fbLogin";	
	}else {
	
	//wrong id, password
	java.util.ArrayList<userData> data=userService.login(ud);
	
	
	if(data.isEmpty()) {		
		return "fbSignup";	
	}else{
		
		//for printing user name generated by system
		
		for(Object o:data) {
			userData e=(userData)o;	
		System.out.println(e);
			//for comman object
			
			allUserDetail.setId(e.getId());
			allUserDetail.setFbName(e.getFbName());
			allUserDetail.setName(e.getName());
			allUserDetail.setEmail(e.getEmail());
			allUserDetail.setPassword(e.getPassword());
			allUserDetail.setBook(e.getBook());
			allUserDetail.setMovie(e.getMovie());
			allUserDetail.setPlace(e.getPlace());
			allUserDetail.setSong(e.getSong());
			allUserDetail.setDp(e.getDp());
			allUserDetail.setFollowers(e.getFollowers());
			allUserDetail.setFollowing(e.getFollowing());
			allUserDetail.setPost(e.getPost());
			
			
			System.out.println("dp name   =   "+allUserDetail);
			 model.addAttribute("fbname",e.getFbName());
			 model.addAttribute("book",e.getBook());
			 model.addAttribute("movie",e.getMovie());
			 model.addAttribute("place",e.getPlace());
			 model.addAttribute("song",e.getSong());
			 model.addAttribute("dp",e.getDp());
			 model.addAttribute("followers",e.getFollowers());
			 model.addAttribute("following",e.getFollowing());
			 model.addAttribute("post",e.getPost());
			 model.addAttribute("post",e.getPost());
			 
          }
		
				return "fbUserProfile";
	}
	}
}



@RequestMapping("/saveRemainDetail")
public String test8(@RequestParam("dp") MultipartFile file, 
		@RequestParam("book") String book,
		@RequestParam("movie") String movie,
		@RequestParam("song") String song,
		@RequestParam("place") String place,
		Model model){	

				 //saving image into DB
	
	
		if(file.isEmpty()) {
			
		} else{
			String filename=file.getOriginalFilename();
		System.out.println(filename+"DONE");
		try {
		

			
		     InputStream is= file.getInputStream();
		     byte data[]=new byte[is.available()];
		     is.read(data);
		     //write
		     FileOutputStream fos=new FileOutputStream(UPLOAD_DIR+File.separator+file.getOriginalFilename());
			fos.write(data);
			fos.flush();
			fos.close();
			allUserDetail.setDp(filename);
		}catch(Exception e) {
		e.printStackTrace();
		}
	 
		}
		allUserDetail.setBook(book);
		allUserDetail.setMovie(movie);
		allUserDetail.setPlace(place);
		allUserDetail.setSong(song);
		
	

	
	 userService.doregister(allUserDetail);
	 
	 
	 model.addAttribute("fbname",allUserDetail.getFbName());
	 model.addAttribute("book",allUserDetail.getBook());
	 model.addAttribute("movie",allUserDetail.getMovie() );
	 model.addAttribute("place",allUserDetail.getPlace());
	 model.addAttribute("song",allUserDetail.getSong());  
	 model.addAttribute("followers",allUserDetail.getFollowers());
	 model.addAttribute("following",allUserDetail.getFollowing());
	 model.addAttribute("dp",allUserDetail.getDp());
	 model.addAttribute("post",allUserDetail.getPost());
	    return "fbUserProfile";

}

@RequestMapping("/forsearch")
public String search(userData findname,Model model){
	
System.out.println(findname.getName());
	 searchresult = userService.search(findname.getName());
     model.addAttribute("sr",searchresult);
     model.addAttribute("followers",allUserDetail.getFollowers());
	 model.addAttribute("following",allUserDetail.getFollowing());
	 System.out.println("iiiiiiiiiiiiiiii"+searchresult);
 return "searchResult";
}


@RequestMapping("/detail")
public String test9(@RequestParam("id") String id,Model model){
	System.out.println(id);
	fid=Integer.parseInt(id);
	String loginUserId=Integer.toString(allUserDetail.getId());
	if(loginUserId.equals(id)) {
		 model.addAttribute("fbname",allUserDetail.getFbName());
		 model.addAttribute("book",allUserDetail.getBook());
		 model.addAttribute("movie",allUserDetail.getMovie() );
		 model.addAttribute("place",allUserDetail.getPlace());
		 model.addAttribute("song",allUserDetail.getSong());  	 
		 model.addAttribute("dp",allUserDetail.getDp());
		 model.addAttribute("followers",allUserDetail.getFollowers());
		 model.addAttribute("following",allUserDetail.getFollowing());
		 model.addAttribute("post",allUserDetail.getPost());
		 
	 return "fbUserProfile"; 
	}
	for(userData o:searchresult) {
		String i=Integer.toString(o.getId());
			if(i.equals(id)) {
				System.out.println(o);
				model.addAttribute("id",o.getId());
				model.addAttribute("n",o.getName());
				model.addAttribute("b",o.getBook());
				model.addAttribute("m",o.getMovie());
				model.addAttribute("s",o.getSong());
				model.addAttribute("p",o.getPlace());
				model.addAttribute("flwrs",o.getFollowers());
				model.addAttribute("flwing",o.getFollowing());
				model.addAttribute("dp",o.getDp());
				model.addAttribute("post",o.getPost());
				Boolean flag=requestservice.doRequestOrNot( allUserDetail.getId(),fid);
				
				if(flag==true) { 
					String s="dont";
				model.addAttribute("flag", s);
				}else
				{
					if(flag==false) { 
						String s="do";
					model.addAttribute("flag", s);
					}
				}
				
				return "fbSeeDetails";
			}
		}
	
	
 return "searchResult";
}



@RequestMapping("/back")
public String test10(Model model){

	 model.addAttribute("fbname",allUserDetail.getFbName());
	 model.addAttribute("book",allUserDetail.getBook());
	 model.addAttribute("movie",allUserDetail.getMovie() );
	 model.addAttribute("place",allUserDetail.getPlace());
	 model.addAttribute("song",allUserDetail.getSong());  	 
	 model.addAttribute("dp",allUserDetail.getDp());
	 model.addAttribute("followers",allUserDetail.getFollowers());
	 model.addAttribute("following",allUserDetail.getFollowing());
	 model.addAttribute("dp",allUserDetail.getDp());
	 model.addAttribute("post",allUserDetail.getPost());
 return "fbUserProfile";
}

//---------------------------------------------------------


@RequestMapping("/notification")
public String notification(Model model){
	
	ArrayList<Request> friendrequest=requestservice.HowManyRequest(allUserDetail.getId(),false);	
	List<userData> sender=new ArrayList<>();
	
	for(Request o:friendrequest) {
		 userData temp=new userData();
		 temp=userService.friendRequestDetail(o.getSenderId());		 
		 sender.add(temp);	
		 System.out.println("sendersssssssssssssssss"+sender);
	}
	
	
	model.addAttribute("requestreceive",sender);
	System.out.println("myyyyyyyyyyyyyyyyyyyyyyy"+friendrequest);
    return "fbNotification";
}


@GetMapping("/reqaccepted")
public String requestAccepted(@RequestParam("id") String id){
	System.out.println("requestAccepted"+id);
	System.out.println("iiiiii"+allUserDetail.getId());
	Request al= requestservice.acceptReq(Integer.parseInt(id),allUserDetail.getId());	
	al.setStatus(true);
	int followers=allUserDetail.getFollowers();
	followers++;
	 allUserDetail.setFollowers(followers);
	  
	 userService.doregister(allUserDetail);
	 requestservice.dorequest(al);
	
	// increase following of sender
	 userData ud=userService.friendRequestDetail(Integer.parseInt(id));
	 System.out.println("inc. following of "+ud);
	 int following =ud.getFollowing();
	 following++;
	 ud.setFollowing(following);
	 userService.doregister(ud);
	return "fbHome";
}

//////////////////////?????????????????????????????????????

@GetMapping("/reqDeletedFromNotification")
public String requestDeleted(@RequestParam("id") String id){
	
	System.out.println("done..............."+id);
	requestservice.deleteRequest(Integer.parseInt(id),allUserDetail.getId());
	return "fbHome";
}




@RequestMapping("request")
public String request1(@RequestParam("id") String id ,Model model){
	fid=Integer.parseInt(id);
	Request req=new Request();
	req.setReceiverId(Integer.parseInt(id));
	req.setSenderId(allUserDetail.getId());
	req.setStatus(false);
	
	Boolean flag=requestservice.doRequestOrNot( allUserDetail.getId(),fid);
	
	System.out.println("sent req"+flag);
	
	if(flag==false) {
	requestservice.dorequest(req);
	}
	return "fbHome";
}

@RequestMapping("/reqdelete")
public String requestDlt(Request rq ,Model model){
	System.out.println(rq.getId());
	System.out.println("senderId  "+allUserDetail.getId()+"receiverId  "+fid);
		requestservice.deleteRequest(allUserDetail.getId(),fid);
		
	return "fbHome";
}

//--------------------------POST-----------------------------------------


@RequestMapping("/savepost")
public String savePost(@RequestParam("userpost") MultipartFile file,Model model){
	
	System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}");
	
	if(file.isEmpty()){
		
	}else {
		String filename=file.getOriginalFilename();
		System.out.println(filename+"DONE");
		
		try {
             InputStream is= file.getInputStream();
		     byte data[]=new byte[is.available()];
		     is.read(data);
		     //write
		     FileOutputStream fos=new FileOutputStream(UPLOAD_DIR+File.separator+file.getOriginalFilename());
			fos.write(data);
			
			SaveUserPost  saveuserpost=new SaveUserPost();
			saveuserpost.setUserid(allUserDetail.getId());
			saveuserpost.setUserpost(filename);
			
			saveUserPostService.saveuserpost(saveuserpost);
			
			int post=allUserDetail.getPost();
			post++;
			
			allUserDetail.setPost(post);
			
			userService.doregister(allUserDetail);
			
			
			fos.flush();
			fos.close();
		}catch(Exception e) {
		e.printStackTrace();
		}
	 
		}
	
	
	ArrayList<Request> al=requestservice.howManyFriends(allUserDetail.getId(), true);
	System.out.println("my friends have =  "+al);
	ArrayList<SaveUserPost> posts=new ArrayList<>();
	for(Request r:al) {
		ArrayList <SaveUserPost> sup=saveUserPostService.getFriendsPost(r.getReceiverId());
		for(SaveUserPost s : sup) {
			posts.add(s);
			}
	}
	
	System.out.println("my frnds post  :   "+posts);
	
	 model.addAttribute("post",posts);
	
	
	 model.addAttribute("name",allUserDetail.getName());
			return "fbUserHome";
}

//************************Like****************************

@GetMapping("/liked")
public String dolike(@RequestParam("id") String id)
{
	int postid=Integer.parseInt(id);
	System.out.println("done..............."+id);
	LikeCommentOnPost l=new LikeCommentOnPost();
	l.setComment(null);
	l.setLike(true);
	l.setPostid(postid);
	l.setUserid(allUserDetail.getId());
	l.setCommentbyname(allUserDetail.getFbName());
	likeCommentService.like(l);
	
	 SaveUserPost sup=saveUserPostService.getPostById(postid);
	 
	 int like=sup.getLike();
	 like++;
	 SaveUserPost newsup=new SaveUserPost();
	 newsup.setId(sup.getId());
	 newsup.setLike(like);
	 newsup.setUserid(sup.getUserid());
	 newsup.setUserpost(sup.getUserpost());
	 saveUserPostService.saveuserpost(newsup);
	return "fbHome";
}


@GetMapping("/commentid")
public String commentid(@RequestParam("id") int id)
{
	System.out.println("tasksssssssssssss   1 ");
System.out.println(id);

postid=id;
System.out.println("hhhhhhhhh"+postid);
return "fbHome";
}



@GetMapping("/commentdsc")
public String commentdsc(@RequestParam("cmt") String cmt)
{
	dsc=cmt;
	LikeCommentOnPost data=new LikeCommentOnPost();
	data.setComment(cmt);
	data.setLike(false);
   data.setPostid(postid);
	data.setUserid(allUserDetail.getId());
	data.setCommentbyname(allUserDetail.getFbName());
	 likeCommentService.like(data);
	 System.out.println("!!!!!!!!!!!!!!!!!!!!!"+data);
	System.out.println("tasksssssssssssss  2  ");
System.out.println(cmt);
return "fbHome";
}

//
@RequestMapping("/mypost")
public String mypost(Model model){
	ArrayList <SaveUserPost> sup=saveUserPostService.getFriendsPost(allUserDetail.getId());
	System.out.println(sup);
    model.addAttribute("mypost", sup);
	return "loginPost";
}


@RequestMapping("/seecomment")
public ResponseEntity<ArrayList<LikeCommentOnPost>> seepostcomment(@RequestParam("id") int id){

	System.out.println(id+"my comment");
	ArrayList<LikeCommentOnPost> comment=new ArrayList<>();
	ArrayList<LikeCommentOnPost> a=likeCommentService.getComment(id);
	for(LikeCommentOnPost c:a) {
		String cm=c.getComment();
		String cn=c.getCommentbyname();
		if(cm==null || cn==null) {
			
			
		}else {
			comment.add(c);
		}
	}

	System.out.println(comment+"my comment");
	return ResponseEntity.ok(comment);

}


}


















