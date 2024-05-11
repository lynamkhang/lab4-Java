package student_management;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionEvent;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;


public class StudentManagement extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtStudentID;
	private JTextField txtName;
	private JTextField txtBirthday;
	private JTable tblStudentList;
	DefaultTableModel model;
	JButton btnDatabase;
	JButton btnAdd;
	JButton btnEdit;
	JButton btnDelete;
	JButton btnClose;
	JButton btnCancel;
	JButton searchBtn;
	
	private JDialog  dialogSearch;
	private JTextField txtMSSVSearch;
	private JTextField txtNameSearch;
	
	SimpleDateFormat date_format = new SimpleDateFormat("dd/mm/yyyy");
	private JLabel lblMath;
	private JLabel lblPhysic;
	private JLabel lblChemistry;
	private JTextField txtMath;
	private JTextField txtPhysic;
	private JTextField txtChemistry;

	
	public boolean isValidDateFormat(String date) {
	    try {
	        date_format.setLenient(false);
	        date_format.parse(date);
	        return true;
	    } catch (ParseException e) {
	        return false;
	    }
	}
	
	public Student getModel() throws ParseException {
		return new Student(txtStudentID.getText(), txtName.getText(), date_format.parse(txtBirthday.getText()), Float.valueOf(txtMath.getText()),Float.valueOf(txtPhysic.getText()), Float.valueOf(txtChemistry.getText()));
	}
		
	public void reset() {
		txtStudentID.setText("");
		txtName.setText("");
		txtBirthday.setText("");
		txtMath.setText("");
		txtPhysic.setText("");
		txtChemistry.setText("");
	}
	
	public boolean isEmptyField() {
		if(txtStudentID.getText().isEmpty() || txtName.getText().isEmpty() || txtBirthday.getText().isEmpty()) {
			return true;
		}
		return false;
	}
	
	public void setModel(Student sv) {
		txtStudentID.setText(sv.getStudentID());
		txtName.setText(sv.getName());
		txtBirthday.setText(date_format.format(sv.getBirthday()));
		txtMath.setText(String.valueOf(sv.getMathScore()));
		txtPhysic.setText(String.valueOf(sv.getPhysicScore()));
		txtChemistry.setText(String.valueOf(sv.getChemistryScore()));
	}
	
	
	public Connection getSQLServerConnection() throws SQLException, ClassNotFoundException {

		//Chỉ định nguồn dữ liệu sẽ kết nối, CSDL Microsoft SQL Server
		//Hostname là localhost và port sẽ là 1433
		String URL = "jdbc:sqlserver://localhost:1433;databaseName=QLSV";
		String USER = "admin";
		String PASSWORD = "1";

		// load driver và register nó với ứng dụng
		// Để đăng ký gọi phương thức: Class.forName(“driverName”);
	    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	    Connection connect = DriverManager.getConnection(URL, USER, PASSWORD);
	    
	    return connect;
	}
	
	public ResultSet getStudentData(Connection connect) throws SQLException {
	    String query = "SELECT * FROM SINHVIEN";
	    PreparedStatement stmt = connect.prepareStatement(query);
	    return stmt.executeQuery();
	}
	
	private void closeConnection(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
		if(conn != null) conn.close();
		if(stmt != null) stmt.close();
		if(rs   != null) rs.close(); 
		
	}
	public Student getStudentByID(String id) throws ClassNotFoundException, SQLException, ParseException {
		Student student = null;
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select * from sinhvien where MASV = ?";
		
		try {
			conn = getSQLServerConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, id);
			rs = stmt.executeQuery();
			while(rs.next()) {
				student = new Student();
				student.setStudentID(rs.getString(1));
				student.setName(rs.getString(2));
				student.setBirthday(date_format.parse(rs.getString(3)));
				student.setMathScore(rs.getFloat(4));
				student.setPhysicScore(rs.getFloat(5));
				student.setChemistryScore(rs.getFloat(6));
				student.setAverage(rs.getFloat(7));
			}
		}
		finally {
			closeConnection(conn,stmt,rs);
		}
		return student;
	}
	
	private void loadRsToTable(final ResultSet rs) throws SQLException {
		DefaultTableModel model = (DefaultTableModel) tblStudentList.getModel();
		model.setRowCount(0);
		
		while (rs.next()) {
			model.addRow(new Object[] {
					rs.getString("MASV"),
					rs.getString("HOTEN"),
					rs.getString("NGSINH"),
					rs.getFloat("DIEMTOAN"),
					rs.getFloat("DIEMLY"),
					rs.getFloat("DIEMHOA"),
					rs.getFloat("DIEMTB")
			});
		}
	}
	
	public boolean validateScore() {
		float mathScore = Float.valueOf(txtMath.getText());
		float physicScore = Float.valueOf(txtPhysic.getText());
		float chemistryScore = Float.valueOf(txtChemistry.getText());
		
		if(mathScore < 0 || mathScore > 10 || physicScore < 0 || physicScore > 10 || chemistryScore < 0 || chemistryScore > 10)
			return false;
		return true;
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StudentManagement frame = new StudentManagement();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StudentManagement() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Quản lý sinh viên");
		setSize(640, 460);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
//		Dialog
		dialogSearch = new JDialog();
		dialogSearch.setTitle("Tìm kiếm sinh viên");
		dialogSearch.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialogSearch.setBounds(100, 100, 382, 244);
		dialogSearch.getContentPane().setLayout(null);
		
		JLabel labelSearch = new JLabel("Thông tin tìm kiếm");
		labelSearch.setFont(new Font("Arial", Font.BOLD, 11));
		labelSearch.setBounds(10, 8, 123, 14);
		dialogSearch.getContentPane().add(labelSearch);
		
		JPanel panelSearch = new JPanel();
		panelSearch.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelSearch.setBounds(10, 23, 346, 171);
		dialogSearch.getContentPane().add(panelSearch);
		panelSearch.setLayout(null);
		
		JLabel mssvSearch = new JLabel("MSSV");
		mssvSearch.setBounds(23, 44, 46, 14);
		panelSearch.add(mssvSearch);
		
		JLabel nameSearch = new JLabel("Tên");
		nameSearch.setBounds(23, 85, 21, 14);
		panelSearch.add(nameSearch);
		
		JCheckBox chckbxMSSVSearch = new JCheckBox("");
		chckbxMSSVSearch.setBounds(70, 40, 21, 23);
		panelSearch.add(chckbxMSSVSearch);
		
		JCheckBox chckbxNameSearch = new JCheckBox("");
		chckbxNameSearch.setBounds(70, 81, 21, 23);
		panelSearch.add(chckbxNameSearch);
		
		txtMSSVSearch = new JTextField();
		txtMSSVSearch.setBounds(114, 41, 179, 20);
		panelSearch.add(txtMSSVSearch);
		txtMSSVSearch.setColumns(10);
		
		txtNameSearch = new JTextField();
		txtNameSearch.setBounds(114, 81, 179, 20);
		panelSearch.add(txtNameSearch);
		txtNameSearch.setColumns(10);
		
		JButton btnSearch = new JButton("OK");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String maSV = txtMSSVSearch.getText();
				String tenSV = txtNameSearch.getText();
				// search
				if(chckbxMSSVSearch.isSelected() && chckbxNameSearch.isSelected()) {
					// both mssv and name
					if(maSV == null || tenSV == null) {
						JOptionPane.showMessageDialog(null,"Vui lòng không để trống thông tin tìm kiếm!","Thông báo",JOptionPane.WARNING_MESSAGE);
					}
					else {
						Connection conn = null;
                        PreparedStatement stmt = null;
                        ResultSet rs = null;
                    	String sql = "SELECT * FROM sinhvien\n"
                    			+ "WHERE MASV = ? and HOTEN = ?";
                        
                    	try {
                    		
                    		conn = getSQLServerConnection();
                			stmt = conn.prepareStatement(sql);
                			
                			stmt.setString(1,maSV);
                			stmt.setString(2,tenSV);
                		
                			rs = stmt.executeQuery();
                			loadRsToTable(rs);
                			dialogSearch.dispose();
                    	} catch (Exception e1) {
							e1.printStackTrace();
                    	}
                    	
                    	finally {
							try {
								closeConnection(conn, stmt, rs);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
                    	}
					}
				}
				else if(chckbxMSSVSearch.isSelected()) {
					// mssv
					if(maSV == null) {
						JOptionPane.showMessageDialog(null,"Vui lòng không để trống thông tin tìm kiếm!","Thông báo",JOptionPane.WARNING_MESSAGE);
					}
					else {
						Connection conn = null;
                        PreparedStatement stmt = null;
                        ResultSet rs = null;
                    	String sql = "SELECT * FROM sinhvien\n"
                    			+ "WHERE MASV = ?";
                        
                    	try {
                    		
                    		conn = getSQLServerConnection();
                			stmt = conn.prepareStatement(sql);
                			
                			stmt.setString(1,maSV);
                		
                			rs = stmt.executeQuery();
                			loadRsToTable(rs);
                			dialogSearch.dispose();
                			
                    	} catch (Exception e1) {
							e1.printStackTrace();
                    	}
                    	
                    	finally {
							try {
								closeConnection(conn, stmt, rs);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
                    	}
						
					}
				}
				
				else {
					// name
					if(tenSV == null) {
						JOptionPane.showMessageDialog(null,"Vui lòng không để trống thông tin tìm kiếm!","Thông báo",JOptionPane.WARNING_MESSAGE);
					}
					else {
						Connection conn = null;
                        PreparedStatement stmt = null;
                        ResultSet rs = null;
                    	String sql = "SELECT * FROM sinhvien\n"
                    			+ "WHERE HOTEN = ?";
                        
                    	try {
                    		
                    		conn = getSQLServerConnection();
                			stmt = conn.prepareStatement(sql);
                			
                			stmt.setString(1,tenSV);
                		
                			rs = stmt.executeQuery();
                			loadRsToTable(rs);
                			dialogSearch.dispose();
                    	} catch (Exception e1) {
							e1.printStackTrace();
                    	}
                    	
                    	finally {
							try {
								closeConnection(conn, stmt, rs);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
                    	}
					}
				}
			}
		});
		btnSearch.setBounds(144, 125, 60, 23);
		panelSearch.add(btnSearch);
		
		JButton btnCancelSearch = new JButton("Cancel");
		btnCancelSearch.setBounds(214, 125, 79, 23);
		panelSearch.add(btnCancelSearch);
		
		btnCancelSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialogSearch.dispose();
			}
			});

		
		//Add student info
        JPanel studentInfoPanel = new JPanel();
        studentInfoPanel.setBounds(10, 22, 604, 188);
        contentPane.add(studentInfoPanel);
        studentInfoPanel.setBorder(new TitledBorder("Thông tin sinh viên"));
        studentInfoPanel.setLayout(null);
        
        txtStudentID = new JTextField();
        txtStudentID.setFont(new Font("Arial", Font.PLAIN, 14));
        txtStudentID.setBounds(126, 33, 150, 25);
        studentInfoPanel.add(txtStudentID);
        txtStudentID.setColumns(10);
        
        JLabel lblStudentID = new JLabel("Mã số sinh viên:");
        lblStudentID.setFont(new Font("Arial", Font.PLAIN, 14));
        lblStudentID.setBounds(10, 33, 118, 25);
        studentInfoPanel.add(lblStudentID);
        
        JLabel lblName = new JLabel("Tên sinh viên:");
        lblName.setFont(new Font("Arial", Font.PLAIN, 14));
        lblName.setBounds(10, 69, 118, 25);
        studentInfoPanel.add(lblName);
        
        txtName = new JTextField();
        txtName.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtName.setColumns(10);
        txtName.setBounds(126, 69, 150, 25);
        studentInfoPanel.add(txtName);
        
        txtBirthday = new JTextField();
        txtBirthday.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtBirthday.setColumns(10);
        txtBirthday.setBounds(126, 105, 150, 25);
        studentInfoPanel.add(txtBirthday);
        
        JLabel lblBirthDay = new JLabel("Ngày sinh:");
        lblBirthDay.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBirthDay.setBounds(10, 105, 118, 25);
        studentInfoPanel.add(lblBirthDay);
        
        //Add Student list
        JPanel studentListPanel = new JPanel();
        studentListPanel.setBounds(10, 222, 604, 155);
        contentPane.add(studentListPanel);
        studentListPanel.setBorder(new TitledBorder("Danh sách sinh viên"));
        studentListPanel.setLayout(null);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 22, 584, 128);
        studentListPanel.add(scrollPane);
        
        tblStudentList = new JTable();
        tblStudentList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int id = tblStudentList.rowAtPoint(e.getPoint());
				
				String studentID = (String) tblStudentList.getValueAt(id,0);
				Student student = null;
				try {
					student = getStudentByID(studentID);
				} catch (SQLException | ClassNotFoundException | ParseException ex) {
					ex.printStackTrace();
				} 
				setModel(student);
			}
		});

        model = new DefaultTableModel();
        Object[] column = {"MSSV", "Tên sinh viên", "Ngày sinh", "Toán", "Lý", "Hóa", "ĐTB"};
        model.setColumnIdentifiers(column);
        tblStudentList.setModel(model);
        scrollPane.setViewportView(tblStudentList);
        
        btnAdd = new JButton("Thêm");
        btnAdd.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(isEmptyField() == true) {
					JOptionPane.showMessageDialog(null, "Vui lòng không được bỏ trống các ô thông tin!!!","Thông báo",JOptionPane.WARNING_MESSAGE);
				}
				else {
					Student sv = null;
					try {
						sv = getStudentByID(txtStudentID.getText());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					if(sv != null) {
						JOptionPane.showMessageDialog(null, "Sinh viên có mã số " +txtStudentID.getText()+" đã tồn tại. Vui lòng kiểm tra lại!!" , "Cảnh báo", JOptionPane.WARNING_MESSAGE);
				
					}
					else if (!isValidDateFormat(txtBirthday.getText())) {
			                JOptionPane.showMessageDialog(null, "Ngày sinh không đúng định dạng, vui lòng nhập lại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			         }
					 else {
						 try {
			                    String[] parts = txtBirthday.getText().split("/");
			                    int year = Integer.parseInt(parts[2]);
			                    
			                    if (year < 1000 || year > 9999) {
			                 	
			                    	JOptionPane.showMessageDialog(null, "Ngày sinh không đúng định dạng, vui lòng nhập lại", "Thông báo", JOptionPane.WARNING_MESSAGE);
			   			         }
			                    
			                    else if(!validateScore()){
			                    	JOptionPane.showMessageDialog(null, "Điểm số nhập không hợp lệ, vui lòng nhập lại", "Thông báo", JOptionPane.WARNING_MESSAGE);
				   
			                    }
			                    
			                    else {
			                        Connection conn = null;
			                        PreparedStatement stmt = null;
			                        ResultSet rs = null;
			                    	String sql = "insert into sinhvien values(?,?,?,?,?,?,?)";
			                        
			                    	try {
			                    		Student student_1 = getModel();
			                			
			                    		conn = getSQLServerConnection();
			                			stmt = conn.prepareStatement(sql);
			                			
			                			stmt.setString(1, student_1.getStudentID());
			                			stmt.setString(2,student_1.getName());
			                			stmt.setString(3,date_format.format(student_1.getBirthday()));
			                			stmt.setFloat(4,student_1.getMathScore());
			                			stmt.setFloat(5, student_1.getPhysicScore());
			                			stmt.setFloat(6,student_1.getChemistryScore());
			                			stmt.setFloat(7, student_1.getAverage());
			                			
			                			stmt.execute();
			                			JOptionPane.showMessageDialog(null, "Thêm sinh viên " + student_1.getStudentID() + " thành công", "Message", JOptionPane.INFORMATION_MESSAGE);
			                			conn = getSQLServerConnection();
			                            if (conn != null) {
			                            	 rs = getStudentData(conn);
			                                 loadRsToTable(rs); 
			                            }
			                		}
			                		finally {
			                			closeConnection(conn,stmt,rs);
			                		}
			                    }
						 }
						 catch (Exception e2) {
						}
				
					 }
				}
			}
        });
        btnAdd.setFont(new Font("Arial", Font.PLAIN, 11));
        btnAdd.setBounds(307, 141, 80, 23);
        studentInfoPanel.add(btnAdd);
        btnAdd.setEnabled(false);
        
        btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		try {
					if(getStudentByID(txtStudentID.getText()) == null) {
						JOptionPane.showMessageDialog(null, "Bạn vui lòng chọn sinh viên trong danh sách!!!" , "Thông báo", JOptionPane.WARNING_MESSAGE);
					}
					else {
						if (!isValidDateFormat(txtBirthday.getText())) {
			                JOptionPane.showMessageDialog(null, "Ngày sinh không đúng định dạng, vui lòng nhập lại!", "Thông báo", JOptionPane.WARNING_MESSAGE);
						}
						else {

							 try {
				                    String[] parts = txtBirthday.getText().split("/");
				                    int year = Integer.parseInt(parts[2]);
				                    
				                    if (year < 1000 || year > 9999) {
				                    	
				                    	JOptionPane.showMessageDialog(null, "Ngày sinh không đúng định dạng, vui lòng nhập lại", "Thông báo", JOptionPane.WARNING_MESSAGE);
				   			         } 
				                    
				                    else if(!validateScore()) {
				                    	JOptionPane.showMessageDialog(null, "Điểm số nhập không hợp lệ, vui lòng nhập lại", "Thông báo", JOptionPane.WARNING_MESSAGE);
				     				}
				                    else {
				                        Connection conn = null;
				                        PreparedStatement stmt = null;
				                        ResultSet rs = null;
				                    	String sql = "UPDATE sinhvien\n"
				                    			+ "SET HOTEN = ?, NGSINH = ?, DIEMTOAN = ?, DIEMLY = ?, DIEMHOA = ?, DIEMTB = ?\n"
				                    			+ "WHERE MASV = ?";
				                        
				                    	try {
				                    		Student student_1 = getModel();
				                			
				                    		conn = getSQLServerConnection();
				                			stmt = conn.prepareStatement(sql);
				                			
				                			stmt.setString(1, student_1.getName());
				                			stmt.setString(2,date_format.format(student_1.getBirthday()));
				                			stmt.setFloat(3,student_1.getMathScore());
				                			stmt.setFloat(4, student_1.getPhysicScore());
				                			stmt.setFloat(5,student_1.getChemistryScore());
				                			stmt.setFloat(6, student_1.getAverage());
				                			stmt.setString(7,student_1.getStudentID());
				                			
				                			stmt.execute();
				                			JOptionPane.showMessageDialog(null, "Cập nhật sinh viên " + student_1.getStudentID() + " thành công", "Message", JOptionPane.INFORMATION_MESSAGE);
				                			conn = getSQLServerConnection();
				                            if (conn != null) {
				                            	 rs = getStudentData(conn);
				                                 loadRsToTable(rs); 
				                            }
				                		}
				                		finally {
				                			closeConnection(conn,stmt,rs);
				                		}
				                    }
							 }
							 catch (Exception e2) {
							}
						}
						
						Student sv = getModel();
	
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
        });
        btnEdit.setFont(new Font("Arial", Font.PLAIN, 11));
        btnEdit.setBounds(397, 141, 80, 23);
        studentInfoPanel.add(btnEdit);
        btnEdit.setEnabled(false);
        
        btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		String id = txtStudentID.getText();
				Student sv = null;
				try {
					sv = getStudentByID(id);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				if(sv == null) {
					JOptionPane.showMessageDialog(null, "Bạn vui lòng chọn sinh viên trong danh sách!" , "Thông báo", JOptionPane.WARNING_MESSAGE);
					
				}
				else {
					int choice = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa sinh viên " + sv.getName() + " không?" ,"Question", JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {

						Connection conn = null;
                        PreparedStatement stmt = null;
                        ResultSet rs = null;
                    	String sql = "DELETE FROM sinhvien\n"
                    			+ "WHERE MASV = ?";
						
                    	try {	
                    		conn = getSQLServerConnection();
                			stmt = conn.prepareStatement(sql);
                			stmt.setString(1,id);
                			stmt.execute();
                			conn = getSQLServerConnection();
                            if (conn != null) {
                            	 rs = getStudentData(conn);
                                 loadRsToTable(rs); 
                            }
                			reset();						
                		}catch (Exception e1) {
							e1.printStackTrace();
						}
                		finally {
                			try {
								closeConnection(conn,stmt,rs);
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
                		}
					}
				}
        	}
        });
        btnDelete.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDelete.setBounds(487, 141, 80, 23);
        studentInfoPanel.add(btnDelete);
        btnDelete.setEnabled(false);
        
        lblMath = new JLabel("Điểm Toán");
        lblMath.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMath.setBounds(306, 33, 118, 25);
        studentInfoPanel.add(lblMath);
        
        lblPhysic = new JLabel("Điểm Lý");
        lblPhysic.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPhysic.setBounds(306, 69, 118, 25);
        studentInfoPanel.add(lblPhysic);
        
        lblChemistry = new JLabel("Điểm Hóa");
        lblChemistry.setFont(new Font("Arial", Font.PLAIN, 14));
        lblChemistry.setBounds(306, 105, 118, 25);
        studentInfoPanel.add(lblChemistry);
        
        txtMath = new JTextField();
        txtMath.setFont(new Font("Arial", Font.PLAIN, 14));
        txtMath.setColumns(10);
        txtMath.setBounds(417, 36, 150, 25);
        studentInfoPanel.add(txtMath);
        
        txtPhysic = new JTextField();
        txtPhysic.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtPhysic.setColumns(10);
        txtPhysic.setBounds(417, 72, 150, 25);
        studentInfoPanel.add(txtPhysic);
        
        txtChemistry = new JTextField();
        txtChemistry.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        txtChemistry.setColumns(10);
        txtChemistry.setBounds(417, 108, 150, 25);
        studentInfoPanel.add(txtChemistry);
        
        btnDatabase = new JButton(" Mở CSDL");
        btnDatabase.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                try {
                    Connection connect = getSQLServerConnection();
                    if (connect != null) {
                        ResultSet rs = getStudentData(connect);
                        loadRsToTable(rs);
						searchBtn.setEnabled(true);
						btnAdd.setEnabled(true);
						btnEdit.setEnabled(true);
						btnDelete.setEnabled(true);
						JOptionPane.showMessageDialog(null,"Mở dữ liệu trong CSDL thành công!");
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                } 
            }
        });
        btnDatabase.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDatabase.setBounds(20, 388, 90, 23);
        contentPane.add(btnDatabase);
        
        btnClose = new JButton("Thoát");
        btnClose.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		System.exit(0);
        	}
        });
        btnClose.setFont(new Font("Arial", Font.PLAIN, 11));
        btnClose.setBounds(534, 387, 80, 23);
        contentPane.add(btnClose);  
        
        searchBtn = new JButton(" Tìm kiếm");
        searchBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dialogSearch.setVisible(true);
        	}
        });
        searchBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        searchBtn.setBounds(120, 388, 90, 23);
        contentPane.add(searchBtn);
        searchBtn.setEnabled(false);
           
	}
}
