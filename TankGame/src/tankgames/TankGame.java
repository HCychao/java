package tankgames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;

public class TankGame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	boolean startnum = false;//判断是否第一次执行开始游戏菜单
	MyPanel mp;
	MyStartPanel msp = null;
//	MyStartPanel myStartPanel = new MyStartPanel();
	JMenuBar jmb = null;		//菜单条
	JMenu jm1 = null;			//菜单
	JMenuItem jmi1 = null;		//菜单项

	public static void main(String[] args) {
		new TankGame().setVisible(true);

	}
	
	public TankGame(){
		this.setTitle("坦克大战");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setBounds(200, 50, 750, 650);
		
		//创建我的开始面板对象，并添加到我的窗体里面
		msp = new MyStartPanel();
		this.add(msp);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		Thread t = new Thread(msp);
		t.start();
		
		//创建菜单
		jmb = new JMenuBar();
		jm1 = new JMenu("游戏(G)");
		jm1.setMnemonic('G');   	//设置快捷键
		jmi1 = new JMenuItem("开始新游戏(N)");
		jmi1.setMnemonic('N');		//快捷键
		jm1.add(jmi1);			//将菜单项添加到菜单中
		jmb.add(jm1);			//将菜单添加到菜单条中
		this.setJMenuBar(jmb);	//将菜单条添加到窗体中
		
		//注册键盘事件监听
//		this.addKeyListener(mp);
		jmi1.addActionListener(this);//为开始新游戏菜单项注册监听
		jmi1.setActionCommand("newgame");//设置其ActionCommand值为"newgame"
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("newgame")){
			if(startnum){//如果不是第一次执行，则先清空敌人的坦克及其子弹
				while(!mp.ets.isEmpty()){
					mp.ets.get(0).isLive = false;
					mp.ets.get(0).life = 0;
					mp.ets.remove(0);
//				this.remove(mp);
				}
				while(!EnemyTank.ss.isEmpty()){
					EnemyTank.ss.get(0).isLive = false;
					EnemyTank.ss.remove(0);
				}
				this.remove(mp);//移除上一次的游戏面板
			}
			else{
				this.remove(msp);//移除开始面板
			}
			startnum = true;
			mp = new MyPanel();
			Thread t = new Thread(mp);
			t.start();
			this.remove(msp);
			this.add(mp);
			this.addKeyListener(mp);
			
			Tank et =mp.ets.get(0);
			this.setVisible(true);
		}
		
	}

}

//创建开始面板
class MyStartPanel extends JPanel implements Runnable{
	int times = 0;			//用于控制显示
	public void paint(Graphics g){
		super.paint(g);
		g.fillRect(0, 0, 750, 650);
		//提示信息
		if(times%2 == 0){
			g.setColor(Color.yellow);
			//开关信息的字体
			Font myFont = new Font("华文新魏",Font.BOLD,90);
			g.setFont(myFont);
			g.drawString("坦克大战", 180, 290);
		}
	}
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(150);
			} catch (Exception e) {}
			times++;
			//重画
			this.repaint();
		}
		
	}
}

//创建我的面板子类
class MyPanel extends JPanel implements KeyListener,Runnable{
	private static final long serialVersionUID = 1L;
	int time = 0;
	int level = 1;//用于进入第二关
	int total = 0;//用于统计击中的敌人坦克总数
	//我的坦克
	MyTank myTank;
	//敌人的坦克
	public static int ensize = 3;
	public static int ensize2 = 5;
	ArrayList<EnemyTank> ets = new ArrayList<EnemyTank>();
	//爆炸的图片
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;
	
	//将组件添加到面板容器里
	public MyPanel(){
		this.setLayout(null);
		//创建我的坦克
		myTank = new MyTank();
		//创建敌人的坦克
		for(int i=0;i<ensize;i++){
			EnemyTank et = new EnemyTank(50+i*150,10,5,2);
			Thread t = new Thread(et);
			t.start();
			ets.add(et);
			et.setEts(ets);
		}
		//爆炸图片
		image1 = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bomb_1.gif"));
		image2=Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bomb_2.gif"));
	    image3=Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("bomb_3.gif"));
	}
	
	//在面板容器里画出组件
	public void paint(Graphics g){
		super.paint(g);
		//画边界
		g.setColor(new Color(20,20,20));
		g.fill3DRect(0, 0, 600, 500, false);
		//画我的坦克
		if(myTank.isLive){
			drawTank(myTank,g);
		}
		else if(myTank.life>0){
			drawBomb(myTank, g);
		}
		// 画敌人的坦克
		for (int i = 0; i < ets.size(); i++) {
			EnemyTank et = ets.get(i);
			if (et.isLive) {
				drawTank(et, g);
				// 画出敌人坦克的子弹
				for (int j = 0; j < et.ss.size(); j++) {
					Shot s = et.ss.get(j);
					if (s.isLive) {
						g.setColor(new Color(200, 0, 0));
						g.fill3DRect(s.x, s.y, 2, 2, false);
					} else {
						et.ss.remove(s);
					}
				}
			} else if (et.life > 0) {
				drawBomb(et, g);
			} 
			else {
				ets.remove(et);
			}
		}
		//画出我的坦克子弹
		for(int i=0;i<myTank.ss.size();i++){
			//从子弹集合中取出子弹
			Shot myShot = myTank.ss.get(i);
			//若子弹集不空，且子弹没有消失，画出子弹
			if(myShot != null && myShot.isLive == true){
				g.setColor(new Color(0,0,150));
				g.draw3DRect(myShot.x, myShot.y, 2, 2, false);
			}
			//子弹消失，从子弹集中删除该子弹
			else if(myShot != null && myShot.isLive == false){
				myTank.ss.remove(myShot);
			}
		}
		//画爆炸效果
		g.drawImage(image1, 0, 0, 1, 1,this);
		//画出提示信息
		this.showInfo(g);
		//游戏说明
        g.setColor(Color.black);
        g.drawString("操作说明",615,250);
        String str[] = {"   游戏开始  N", "   退出   ESC","   向上      ↑ /  W", "   向下      ↓ /  S", "   向左      ← /  A", "   向右      → /  D", "   发射子弹    空格","   原地复活    Enter" };
        for(int i=0;i<str.length;i++){
            g.drawString(str[i],605,280+30*i);
        }
        
        // 如果我的坦克三条命都用完，游戏结束
        if (myTank.size == 0) {
            for (int i = 0; i < ets.size(); i++) {
                ets.remove(i);
            }
            g.setColor(Color.black);
            g.fillRect(0, 0, 600, 500);
            // 提示信息
            if (time % 2 == 0)// 通过显示或不显示产生闪光效果
            {
                g.setColor(Color.red);
                // 开关信息的字体
                Font myFont = new Font("仿宋", Font.BOLD, 50);
                g.setFont(myFont);
                g.drawString("Game over！", 200, 250);
            }
        }
        
        // 如果将敌人的坦克都击灭，进入到第二关
        if (total == ensize && level == 1) {

            myTank = new MyTank();
            // 创建敌人的坦克
            for (int i = 0; i < ensize2; i++) {
                EnemyTank et = new EnemyTank(15+i*100,10,5,2);
                et.speed=5;
                ets.add(et);
                Thread t = new Thread(et);
                t.start();
                // 将MyPanel的敌人坦克向量交给该敌人坦克
                et.setEts(ets);
            }
            level = 0;
        }
        
        if (total == 3 + ensize2) {
            g.setColor(Color.black);
            g.fillRect(0, 0, 600, 500);
            // 提示信息
            if (time % 2 == 0)// 通过显示或不显示产生闪光效果
            {
                g.setColor(Color.CYAN);
                // 开关信息的字体
                Font myFont = new Font("仿宋", Font.BOLD, 50);
                g.setFont(myFont);
                g.drawString("胜利！！", 200, 250);
            }
        }
	}
	
	//画坦克
	public void drawTank(Tank t,Graphics g){
		int x = t.x, y = t.y, w = t.w, h = 12*t.w;
		//画出向上的坦克
		if(t.direct == 0){
			Graphics2D g2d = (Graphics2D)g;
			g.setColor(t.c1);
			g.fill3DRect(x, y, w, h, false);
			g.fill3DRect(x+7*w, y, w, h, false);
			g.setColor(t.c2);
			g.fill3DRect(x+w, y+2*w, 6*w, 8*w, false);
			g.fillOval(x+2*w, y+4*w, 4*w, 4*w);
			g2d.setColor(t.c3);
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x+4*w, y, x+4*w, y+6*w);
		}
		//画出向下的坦克
		else if(t.direct == 2){
			Graphics2D g2d = (Graphics2D)g;
			g.setColor(t.c1);
			g.fill3DRect(x, y, w, h, false);
			g.fill3DRect(x+7*w, y, w, h, false);
			g.setColor(t.c2);
			g.fill3DRect(x+w, y+2*w, 6*w, 8*w, false);
			g.fillOval(x+2*w, y+4*w, 4*w, 4*w);
			g2d.setColor(t.c3);
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x+4*w, y+6*w, x+4*w, y+12*w);
		}
		//画出向左的坦克
		else if(t.direct == 3){
			Graphics2D g2d = (Graphics2D)g;
			g.setColor(t.c1);
			g.fill3DRect(x, y, h, w, false);
			g.fill3DRect(x, y+7*w, h, w, false);
			g.setColor(t.c2);
			g.fill3DRect(x+2*w, y+w, 8*w, 6*w, false);
			g.fillOval(x+4*w, y+2*w, 4*w, 4*w);
			g2d.setColor(t.c3);
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x, y+4*w, x+6*w, y+4*w);
		}
		//画出向右的坦克
		else if(t.direct ==1){
			Graphics2D g2d = (Graphics2D)g;
			g.setColor(t.c1);
			g.fill3DRect(x, y, h, w, false);
			g.fill3DRect(x, y+7*w, h, w, false);
			g.setColor(t.c2);
			g.fill3DRect(x+2*w, y+w, 8*w, 6*w, false);
			g.fillOval(x+4*w, y+2*w, 4*w, 4*w);
			g2d.setColor(t.c3);
			g2d.setStroke(new BasicStroke(5.0f));
			g2d.drawLine(x+6*w, y+4*w, x+12*w, y+4*w);
		}
	}
	
	//判断子弹是否击中坦克
	public boolean isHitTank(Shot s,Tank t){
		switch(t.direct){
		case 0:
		case 2:
			if(s.x>t.x&&s.x<t.x+8*t.w&&s.y>t.y&&s.y<t.y+t.h){
				s.isLive = false;
				t.isLive = false;
				return true;
			}
			break;
		case 1:
		case 3:
			if(s.x>t.x&&s.x<t.x+t.h&&s.y>t.y&&s.y<t.y+8*t.w){
				s.isLive = false;
				t.isLive = false;
				return true;
			}
			break;
		}
		return false;
	}
	
	//判断我的子弹是否击中敌人的坦克
	public void hitEnemyTank(){
		Shot s = null;
		for(int i=0;i<myTank.ss.size();i++){
			s = myTank.ss.get(i);
			if(s.isLive){
				for(int j=0;j<ets.size();j++){
					EnemyTank et =ets.get(j);
					if(et.isLive){
						if(this.isHitTank(s, et)){
							total++;
						}
					}
				}
			}
		}
	}
	//判断敌人坦克的子弹是否击中我的坦克
	public boolean hitMyTank(){
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			for(int j=0;j<et.ss.size();j++){
				Shot s = et.ss.get(j);
				if(myTank.isLive){
					if(isHitTank(s, myTank)){
						return true;
					}
				}
			}
		}
		return false;
	}
	//画爆炸效果方法
	public void drawBomb(Tank t ,Graphics g){
		if(t.life>2){
			g.drawImage(image1, t.x, t.y, 90, 90, this);
		}
		else if(t.life>1){
			g.drawImage(image2, t.x, t.y, 60, 60, this);
		}
		else if(t.life>0){
			g.drawImage(image3, t.x, t.y, 30, 30, this);
		}
		t.life--;
	}
	//画出提示信息
	public void showInfo(Graphics g){
		g.drawString("剩余生命值", 10, 560);
        //敌方tank剩余生命值
        EnemyTank et=new EnemyTank(80,530,4,0);
        this.drawTank(et, g);
        int t = 0;
        for (int i = 0; i < ets.size(); i++) {
            EnemyTank et1 = ets.get(i);
            if (et1.isLive)
                t++;
        }
        g.drawString(t + "", 125, 560);
        //myTank剩余生命值
        MyTank mt = new MyTank(300, 530, 4, 0);
        this.drawTank(mt, g);
        g.drawString(myTank.size + "", 345, 560);
        //my得分
        mt.x = 630;
        mt.y = 100;
        this.drawTank(mt, g);
        g.setColor(Color.red);
        g.drawString("你的成绩为:", 620, 85);
        g.drawString(total + "", 645, 180);
    }
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	//我的坦克移动
	@Override
	public void keyPressed(KeyEvent e) {
		//向左移动
		if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A && myTank.isLive){
			if(!(myTank.x<=0)){
			myTank.x -= myTank.speed;
			myTank.direct = 3;
			myTank.invincible = false;
			}
			else{
				myTank.x = myTank.x;
				myTank.direct = 3;
			}
		}
		//向右移动
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D && myTank.isLive){
			myTank.invincible = false;
			if(myTank.x<600-myTank.h){
				myTank.x += myTank.speed;
				myTank.direct = 1;
			}
			else{
				myTank.x = myTank.x;
				myTank.direct = 1;
			}
		}
		//向上移动
		else if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W && myTank.isLive){
			myTank.invincible = false;
			if(!(myTank.y<=0)){
				myTank.y -= myTank.speed;
				myTank.direct =0;
			}
			else{
				myTank.y = myTank.y;
				myTank.direct = 0;
			}
		}
		//向下移动
		else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S && myTank.isLive){
			myTank.invincible = false;
			if(myTank.y<500-myTank.h){
				myTank.y += myTank.speed;
				myTank.direct = 2;
			}
			else{
				myTank.y = myTank.y;
				myTank.direct = 2;
			}
		}
		//发射子弹
		else if(e.getKeyCode() == KeyEvent.VK_SPACE && myTank.isLive){
			//最多可连发5枚子弹
			if(myTank.ss.size() < 5){
				myTank.shotEnemyTank();
				myTank.invincible = false;
			}
		}
		//关闭游戏
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			System.exit(0);
		}
		//按回車鍵，我的坦克原地復活
		else if(e.getKeyCode() == KeyEvent.VK_ENTER && myTank.isLive == false && myTank.size>0){
			myTank.isLive = true;
			myTank.life = 3;
			myTank.invincible = true;
		}
		//刷新图形
		this.repaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(100);
			} catch (Exception e) {}
			if(myTank.isLive && myTank.invincible == false){
			//击中目标坦克
			this.hitEnemyTank();
			//敌人坦克击中我的坦克
			if(this.hitMyTank()){
				myTank.size--;
				}
			}
			time++;
			//重绘
			this.repaint();
		}
		
	}

}
