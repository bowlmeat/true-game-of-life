package UIPackage;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sys.Cell;
import sys.GameMap;
import sys.GameProcessing;

/**
 * 
 * @author 86173
 *
 */


public class MainFrame extends JFrame {
	private static final int GRID_SIZE=16;//格子边长大小
	private static final int dis = 100;//地图绘画起点位置
	//private JPanel contentPane;
	private boolean change = true;
    private boolean isonestep = false;
	private GameMap map;
	private OptionFrame of=new OptionFrame();
	private GameProcessing gamepro = new GameProcessing();
	
	
	private int formrow=10;
	private int formcol=10;
	
	//定义JPanel内部类,用于完成显示
	public class View extends JPanel{
		
		//绘制
		public void paint(Graphics g) {
			super.paint(g);
			for(int r=0;r<map.getMapRow();r++) {
				for(int c=0;c<map.getMapCol();c++) {
					Cell cell=map.get(r, c);     //获得读取地图内容，填充显示细胞
					if(cell!=null && /*cell.isChangeStatus()*/cell.isAlive()) {
						cell.draw(g, r*GRID_SIZE + dis, c*GRID_SIZE + dis, GRID_SIZE);
					}
				}
			}		
		}	
        //画线
		public void paintGirdLines(Graphics g)
		{
			g.setColor(new Color(0,0,0));
			//画网格 横向
			for(int i=0;i<=map.getMapCol();i++)
			{
    			g.drawLine(dis, i*GRID_SIZE+ dis, map.getMapRow()*GRID_SIZE + dis,i*GRID_SIZE + dis);
			}
			//纵向
			for(int i=0;i<=map.getMapRow();i++)
			{
    			g.drawLine(i*GRID_SIZE+ dis,dis,i*GRID_SIZE+ dis,map.getMapCol()*GRID_SIZE+ dis);
			}
		}
	}
	
	
    //演化线程类
	class ProcessThread extends Thread{
		
		public void run()
		{
			while(change) {
				//清空上一次演化留下的界面
				//clearPaint(getGraphics(),40,40);
				clearPaint(getGraphics(),30,30);
				//clearPaint(getGraphics(),formrow,formcol);
				//演化
				evolve();
				try {
					int count  = of.getRatetextField();  //设置演化速度
					Thread.sleep(count * 50);
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	
	
	
	private View view;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame mainframe = new MainFrame();
					
					mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					mainframe.setResizable(false);
					
					//mainframe.pack();
					mainframe.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("GameofLife");   //设置title（对于test检测很重要）
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 600);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenuItem JMenuItem_clear = new JMenuItem("          清空画面");
		JMenuItem_clear.setHorizontalAlignment(SwingConstants.CENTER);
		JMenuItem_clear.setName("JMenuItem_clear");
		JMenuItem_clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearPaint(getGraphics(),map.getMapRow(),map.getMapCol());
			}
		});
		menuBar.add(JMenuItem_clear);
		
		JMenuItem JMenuItem_random = new JMenuItem("随机生成新画面");
		JMenuItem_random.setName("JMenuItem_random");
		JMenuItem_random.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int r = of.getRowtextField();
				int c = of.getColtextField();
				map = new GameMap(r,c);
				//clearPaint(getGraphics(),40,40);
				//clearPaint(getGraphics(),r,c);
				clearPaint(getGraphics(),30,30);
				
				//clearPaint(getGraphics(),formrow,formcol);
				
				int d = of.getDeathtextField();
				map.randomCellsStatus(d);
				view=new View();	
				//mainframe.add(view); 
				
				view.paintGirdLines(getGraphics());
				
				view.paint(getGraphics());
				getContentPane().add(view);
				getContentPane().setName("ContentPane");
				saverc(r,c);
				
				map.setStatusDie();
			}
		});
		menuBar.add(JMenuItem_random);
		
		JMenuItem JMenuItem_onestep = new JMenuItem("进行单步演化");
		JMenuItem_onestep.setName("JMenuItem_onestep");
		JMenuItem_onestep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isonestep = true;
				//clearPaint(getGraphics(),40,40);
				//clearPaint(getGraphics(),formrow,formcol);
				clearPaint(getGraphics(),30,30);
				
				//clearPaint(getGraphics(),of.getRowtextField(),of.getColtextField());
				
				evolve();
			}
		});
		menuBar.add(JMenuItem_onestep);
		
		
		JMenuItem JMenuItem_stop = new JMenuItem("结束不间断演化");
		JMenuItem_stop.setName("JMenuItem_stop");
		JMenuItem_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				//设定标识符，使演化结束
				change = false;
			}
		});
		menuBar.add(JMenuItem_stop);
		
		JMenuItem JMenuItem_start = new JMenuItem("开始不间断演化   ");
		JMenuItem_start.setName("JMenuItem_start");
		JMenuItem_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				isonestep =false;
				change = true;
				//开启演化细胞线程
				ProcessThread pth = new ProcessThread();
				pth.start();
			
				
			}
		});
		menuBar.add(JMenuItem_start);					
		
		JMenuItem JMenuItem_option = new JMenuItem("           选项");
		JMenuItem_option.setName("JMenuItem_option");
		JMenuItem_option.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				//使OptionFrame可见
				of.setVisible(true);
			}
		});
		
		menuBar.add(JMenuItem_option);
		
		
		JMenuItem JMenuItem_view = new JMenuItem("稀有掉落");
		JMenuItem_view.setName("JMenuItem_view");
		JMenuItem_view.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				JOptionPane.showMessageDialog(null, "注意：此时您不能设置行数列数与死亡率！", "稀有掉落", JOptionPane.INFORMATION_MESSAGE);                     
				//设定特定的行列并生成地图
				of.setRowtextField(17);
				of.setColtextField(17);
				int r = of.getRowtextField();
				int co = of.getColtextField();
				map=new GameMap(r,co);
				
				//设置循环生成地图
			   for(int i=0;i<2;i++) {
				   for(int j=0;j<2;j++) {
					   map.setStatus(2+j*5+i*7, 4, true);
					   map.setStatus(2+j*5+i*7, 5, true);
					   map.setStatus(2+j*5+i*7, 6, true);
					   map.setStatus(2+j*5+i*7, 10, true);
					   map.setStatus(2+j*5+i*7, 11, true);
					   map.setStatus(2+j*5+i*7, 12, true);
				   }				   
				   
				   for(int k=0;k<3;k++) {
					   map.setStatus(4+k+i*6, 2, true);
					   map.setStatus(4+k+i*6, 7, true);
					   map.setStatus(4+k+i*6, 9, true);
					   map.setStatus(4+k+i*6, 14, true);
				   }3
			   }
			   
			   //清空地图，避免先前操作生成地图的影响
			 //  clearPaint(getGraphics(),40,40);				
			 //  clearPaint(getGraphics(),formrow,formcol);
			   
			   //clearPaint(getGraphics(),of.getRowtextField(),of.getColtextField());
			   
			   clearPaint(getGraphics(),30,30);
				view=new View();	
				view.paintGirdLines(getGraphics());
				view.paint(getGraphics());
				getContentPane().add(view);			   
			   			   
		}
		});
		menuBar.add(JMenuItem_view);
		
	}
	
	
	//地图演化
	public void evolve() {		
		int r = of.getRowtextField();
		int co = of.getColtextField();
		gamepro.NextStatus(r, co, map);
		
		this.view.paintGirdLines(getGraphics());
		
		this.view.paint(getGraphics());
		if(isonestep) {
			change = false;
		}			
		
	}
	
	//清空地图
	public void clearPaint(Graphics g,int row,int col)
	{
		g.setColor(new Color(238,238,238));
		for(int i=0;i<=row;i++)
		{
			for(int j=0;j<=col;j++)
			{
			
					g.fillRect(j*GRID_SIZE+dis, i*GRID_SIZE+dis,GRID_SIZE,GRID_SIZE);
				
			}
		}
		
	}
	
	//保存之前的Row和Col值
	public void saverc(int r, int c)
	{
		formrow = r;
		formcol = c;
	}
}






