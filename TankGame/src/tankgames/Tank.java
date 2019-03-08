package tankgames;

import java.awt.*;
import java.util.ArrayList;

//创建坦克类
public class Tank {
	int x,y,w,h;
	int speed;
	int direct;
	boolean isLive = true;
	int life = 3;
	Color c1,c2,c3;
	
	public Tank(){
		x=250;
		y=400;
		w=5;
		h=60;
		speed=5;
		direct=0;
	
	}

	public Tank(int x, int y, int w, int direct) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h =this.w*12;
		this.speed = 5;
		this.direct = direct;
	}
}


//创建我的坦克子类
class MyTank extends Tank{
	//我的坦克数量
	public int size = 3;
	//判斷我的坦克是否处于无敌状态
	public boolean invincible = true;
	//创建颜色对象，并设置颜色
	public MyTank(){
		this.c1 = new Color(128,128,128);
		this.c2 = new Color(0,255,0);
		this.c3 = new Color(0,0,255);
	}
	
	public MyTank(int x, int y, int w, int direct) {
		super(x, y, w, direct);
		this.c1 = new Color(128,128,128);
		this.c2 = new Color(0,255,0);
		this.c3 = new Color(0,0,255);
	}
	//创建子弹集合
	ArrayList<Shot> ss = new ArrayList<Shot>();
	Shot s = null;
	//创建子弹，并启动
	public void shotEnemyTank(){
		//根据坦克的方向确定子弹方向和位置
		switch(direct){
			case 0: s = new Shot((x-1)+4*w,y,direct);ss.add(s);break;
			case 1: s = new Shot(x+h,(y-1)+4*w,direct);ss.add(s);break;
			case 2: s = new Shot((x-1)+4*w,y+h,direct);ss.add(s);break;
			case 3: s = new Shot(x,(y-1)+4*w,direct);ss.add(s);break;
		}
		Thread t = new Thread(s);
		t.start();
	}
}

//敌人坦克类
class EnemyTank extends Tank implements Runnable{
	//敌人坦克的集合
	ArrayList<EnemyTank> ets = new ArrayList<EnemyTank>();
	//敌人子弹的集合(静态子弹集合)
	public static ArrayList<Shot> ss = new ArrayList<Shot>();
	public EnemyTank(){
		this.c1 = new Color(128,128,128);
		this.c2 = new Color(0,255,0);
		this.c3 = new Color(0,0,255);
	}
	
	public EnemyTank(int x, int y, int w, int direct) {
		super(x, y, w, direct);
		this.speed = 3;
		this.c1 = new Color(200,200,120);
		this.c2 = new Color(0,255,127);
		this.c3 = new Color(200,0,0);
	}
	//获取MyPanel的敌人坦克集合
	public void setEts(ArrayList<EnemyTank> ets){
		this.ets = ets;
	}
	//判断坦克之间有没有接触，有就改变方向
	public boolean isTouchOtherTank(Tank t){
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			if(et != this){
				//判断et和当前坦克的距离，若距离<h+15，则改变方向
				if(distance(t,et)<h+15){
					//距离小于坦克的长度+10时修改坦克的方向
					//direct = (direct+1)%4;
					return true;
				}
			}
		}
		return false;
	}
	//计算两坦克的距离
	private int distance(Tank t1, Tank t2) {
		Point p1,p2;
		p1 = centerPoint(t1);
		p2 = centerPoint(t2);
		return (int)(Math.sqrt((p2.x-p1.x)*(p2.x-p1.x)+(p2.y-p1.y)*(p2.y-p1.y)));
	}
	//返回坦克的中心点
	public Point centerPoint(Tank t){
		Point p = new Point(0,0);
		if(t.direct == 0 || t.direct == 2){
			p.x = t.x+4*t.w;
			p.y = t.y+6*t.w;
		}
		else if(t.direct == 1 || t.direct == 3){
			p.x = t.x+6*t.w;
			p.y = t.y+4*t.w;
		}
		return p;
	}
	//让敌人坦克移动的线程
	@Override
	public void run() {
		//让坦克运动
		int num = 0;
		Tank t;		//t为移动后的坦克
		while(true){
			t = new Tank(x,y,w,direct);
			switch(t.direct){
			case 0:t.y -= this.speed;break;
			case 1:t.x += this.speed;break;
			case 2:t.y += this.speed;break;
			case 3:t.x -= this.speed;break;
			}
			if(isTouchOtherTank(t)){
				changeDirect();		//若重叠则改变方向
			}
			else{
				x = t.x;
				y = t.y;
			}
			if(num == 50 || isToBorder()){
				//当坦克沿着一个方向移动一段时间或到达边界后，就改变方向
				num = 0;
				changeDirect();			//改变坦克方向
			}
			num++;
			try {
				Thread.sleep(50);
			} catch (Exception e) {}
			//添加子弹
			if(this.isLive && ss.size()<50 && num%10 == 0){
				Shot s = null;
				switch(direct){
				case 0:s = new Shot((x-1)+4*w,y,direct);break;
				case 1:s = new Shot(x+h,(y-1)+4*w,direct);break;
				case 2:s = new Shot((x-1)+4*w,y+h,direct);break;
				case 3:s = new Shot(x,(y-1)+4*w,direct);break;
				}
				ss.add(s);
				Thread t1 = new Thread(s);
				t1.start();
			}
		}
		
	}
	//坦克改变方向
	private void changeDirect() {
		int d = (int)(Math.random()*4);
		if(d == direct){
			direct = (direct+1)%4;
		}
		else
			direct = d;
	}
	//判断坦克是否到达边界
	private boolean isToBorder() {
		switch (direct) {
		case 0:if(y<4*w)return true;break;
		case 1:if(x+h>600-4*w)return true;break;
		case 2:if(y+h>500-4*w)return true;break;
		case 3:if(x<4*w)return true;break;
		}
		return false;
	}
}

//创建子弹类
class Shot implements Runnable{
	int x;
	int y;
	int direct;
	int speed = 10;
	boolean isLive = true;
	
	public Shot(int x,int y,int direct){
		this.x = x;
		this.y = y;
		this.direct = direct;
	}
	
	//子弹自动移动
	@Override
	public void run() {
		while(true){
			//子弹的线程，每50毫秒移动一次
			try {
				Thread.sleep(50);
			} catch (Exception e) {}
			//判断子弹的方向
			switch(direct){
			case 0:y -= speed;break;
			case 1:x += speed;break;
			case 2:y += speed;break;
			case 3:x -= speed;break;
			}
			//判断子弹的存活
			if(x<=0||y<=0||x>=600||y>=500){
				isLive = false;
				break;
			}
		}
		
	}
	
}
