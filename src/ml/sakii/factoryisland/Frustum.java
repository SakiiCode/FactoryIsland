package ml.sakii.factoryisland;


public class Frustum {
	  Plane[] sides = new Plane[4];          // represent the 4 sides of the frustum
	  Plane znear;             // the z-near plane
	  float hratio;
	  float vratio;
	  private Game game;
	  Vector al, ar, at, ab;
	  Vector ToCamera = new Vector();
	  
	  public Frustum(Game game){
		  this.game = game;
		  znear = new Plane();
		  for(int i=0;i<4;i++){
			  sides[i] = new Plane();
		  }
		  al=new Vector();
		  ar=new Vector();
		  at=new Vector();
		  ab=new Vector();
		  update();
	  }

	  
	  
	  void update(){
		  
		  //al.normalize();
		 /* al = game.ViewVector.add(game.LeftViewVector.multiply(hratio)).normalize();
		  
		  ar = game.ViewVector.add(game.RightViewVector.multiply(hratio)).normalize();
		  
		  at = game.ViewVector.add(game.TopViewVector.multiply(vratio)).normalize();
		  
		  ab = game.ViewVector.add(game.BottomViewVector.multiply(vratio)).normalize();*/
		  
		  Vector ViewFrom = game.PE.getPos();
		  // left
		  al.set(game.LeftViewVector);
		  al.multiply(hratio);
		  al.add(game.ViewVector);
		  al.CrossProduct2(game.BottomViewVector);
		  sides[0].normal.set(al);//game.BottomViewVector.CrossProduct(al);
		  sides[0].distance = sides[0].normal.DotProduct(ViewFrom);
		  
		  // right
		  ar.set(game.RightViewVector);
		  ar.multiply(hratio);
		  ar.add(game.ViewVector);
		  ar.CrossProduct2(game.TopViewVector);
		  sides[1].normal.set(ar);
		  sides[1].distance = sides[1].normal.DotProduct(ViewFrom);
		  
		  // top
		  at.set(game.TopViewVector);
		  at.multiply(hratio);
		  at.add(game.ViewVector);
		  at.CrossProduct2(game.LeftViewVector);
		  sides[2].normal.set(at);
		  sides[2].distance = sides[2].normal.DotProduct(ViewFrom);
		  
		  // bottom
		  ab.set(game.BottomViewVector);
		  ab.multiply(hratio);
		  ab.add(game.ViewVector);
		  ab.CrossProduct2(game.RightViewVector);
		  sides[3].normal.set(ab);
		  sides[3].distance = sides[3].normal.DotProduct(ViewFrom);
		  

		  /*znear.normal.set(game.ViewVector);
		  ToCamera.set(game.ViewVector);
		  ToCamera.multiply(0.01f);
		  ToCamera.add(ViewFrom);//= game.PE.ViewFrom.add(game.ViewVector.multiply(0.01f));
		  znear.distance = znear.normal.DotProduct(ToCamera);*/
		  

	}
}
