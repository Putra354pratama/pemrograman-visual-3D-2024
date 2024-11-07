/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package praktikummodul_5;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AppKaryawan extends javax.swing.JFrame {
    Connection conn;
    private DefaultTableModel modelKaryawan;
    private DefaultTableModel modelProyek;
    private DefaultTableModel modelTransaksi;

    public AppKaryawan() {
        initComponents();
        conn = Koneksi.getConnection();
        initTableModels();
        
        loadDataKaryawan();
        loadDataProyek();
        loadDataTransaksi();
        loadComboBoxKaryawan();
        loadComboBoxProyek();
    
    }
        //menganalisis data
        private void initTableModels() {
            
            modelKaryawan = new DefaultTableModel(new String[] { "ID", "Nama", "Jabatan", "Departemen" }, 0);
            tablekaryawan.setModel(modelKaryawan);

            
            modelProyek = new DefaultTableModel(new String[] { "ID", "Nama Proyek", "Durasi Pengerjaan" }, 0);
            tableproyek.setModel(modelProyek);

            modelTransaksi = new DefaultTableModel(new String[]{"ID Karyawan", "ID Proyek","Peran",},0);
            tabletransaksi.setModel(modelTransaksi);
    }
        
        //mengambil data dari karyawan
        private void loadDataKaryawan() {
        modelKaryawan.setRowCount(0);
        try {
            String sql = "SELECT * FROM karyawan";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelKaryawan.addRow(new Object[]{
                    rs.getInt("id_karyawan"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("departemen")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Load Data Karyawan: " + e.getMessage());
        }
    }
        
        //mengambil data dari proyek
        private void loadDataProyek() {
        modelProyek.setRowCount(0);
        try {
            String sql = "SELECT * FROM proyek";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelProyek.addRow(new Object[]{
                    rs.getInt("id_proyek"),
                    rs.getString("nama_proyek"),
                    rs.getString("durasi_pengerjaan")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error Load Data Proyek: " + e.getMessage());
        }
    }
         
        //mengambil data dari transaksi
        private void loadDataTransaksi() {
        modelTransaksi.setRowCount(0);
          try {
          String sql = "SELECT k.nama AS nama_karyawan, p.nama_proyek, " +
                     "t.peran, t.id_karyawan, t.id_proyek " +
                     "FROM transaksi t " +
                     "JOIN karyawan k ON t.id_karyawan = k.id_karyawan " +
                     "JOIN proyek p ON t.id_proyek = p.id_proyek " +
                     "ORDER BY k.nama, p.nama_proyek";

                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            System.out.println("Data ditemukan: " + rs.getString("nama_karyawan"));
            modelTransaksi.addRow(new Object[]{
                rs.getString("nama_karyawan"),
                rs.getString("nama_proyek"),
                rs.getString("peran"),
                rs.getInt("id_karyawan"),
                rs.getInt("id_proyek")
            });
        }
            } catch (SQLException e) {
                System.out.println("Error Load Data Transaksi: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Gagal memuat data transaksi: " + e.getMessage());
            }
        }

        
            //menambah data karyawan
            private void tambahKaryawan() {
            if (tfnama.getText().isEmpty() || tfjabatan.getText().isEmpty() || tfdepartemen.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tolong silahkan dilengkapi terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String sql = "INSERT INTO karyawan (nama, jabatan, departemen) VALUES (?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tfnama.getText());
                ps.setString(2, tfjabatan.getText());
                ps.setString(3, tfdepartemen.getText());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    tfidkaryawan.setText(String.valueOf(rs.getInt(1)));
                }

                JOptionPane.showMessageDialog(this, "Data Karyawan berhasil ditambahkan.");
                loadDataKaryawan();
                loadComboBoxKaryawan();
                clearKaryawanFields();
            } catch (SQLException e) {
                System.out.println("Error Tambah Data Karyawan: " + e.getMessage());
            }
        }

            
        //Update data Karyawan
        private void updateKaryawan() {
            try {
            int id = Integer.parseInt(tfidkaryawan.getText());

            // Ambil data saat ini dari database
            String sqlSelect = "SELECT * FROM karyawan WHERE id_karyawan = ?";
            PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
            psSelect.setInt(1, id);
            ResultSet rs = psSelect.executeQuery();

            String namaLama = "", jabatanLama = "", departemenLama = "";
            if (rs.next()) {
                namaLama = rs.getString("nama");
                jabatanLama = rs.getString("jabatan");
                departemenLama = rs.getString("departemen");
            }

            // Siapkan SQL update
            String sqlUpdate = "UPDATE karyawan SET nama = ?, jabatan = ?, departemen = ? WHERE id_karyawan = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);

            // Cek apakah field kosong atau tidak
            String namaBaru = tfnama.getText().isEmpty() ? namaLama : tfnama.getText();
            String jabatanBaru = tfjabatan.getText().isEmpty() ? jabatanLama : tfjabatan.getText();
            String departemenBaru = tfdepartemen.getText().isEmpty() ? departemenLama : tfdepartemen.getText();

            // Set nilai baru
            psUpdate.setString(1, namaBaru);
            psUpdate.setString(2, jabatanBaru);
            psUpdate.setString(3, departemenBaru);
            psUpdate.setInt(4, id);

            // Eksekusi update
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data Karyawan berhasil diupdate.");
            loadDataKaryawan();
            loadComboBoxKaryawan();
            clearKaryawanFields();
        } catch (SQLException e) {
            System.out.println("Error Update Data Karyawan: " + e.getMessage());
        }
    }

                
        //hapus data karyawan
        private void hapusKaryawan() {
        try {
            int id = Integer.parseInt(tfidkaryawan.getText());

           
            conn.setAutoCommit(false);

            try {
                
                String sql = "DELETE FROM karyawan WHERE id_karyawan = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();

                // Reset auto increment dan reorder ID
                String resetSql = "SET @count = 0";
                String updateSql = "UPDATE karyawan SET id_karyawan = @count:= @count + 1";
                String alterSql = "ALTER TABLE karyawan AUTO_INCREMENT = 1";

                Statement stmt = conn.createStatement();
                stmt.execute(resetSql);
                stmt.execute(updateSql);
                stmt.execute(alterSql);

                
                conn.commit();

                JOptionPane.showMessageDialog(this, "Data Karyawan berhasil dihapus.");
                loadDataKaryawan();
                loadDataTransaksi();
                loadComboBoxKaryawan();
                clearKaryawanFields();

            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Error Hapus Data Karyawan: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID tidak valid");
        }
    }

        
        // Fungsi untuk menambahkan proyek
        private void tambahProyek() {
            // Cek apakah semua field telah terisi
            if (tfnamaproyek.getText().isEmpty() || tfdurasipengerjaan.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tolong lengkapi data proyek terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String sql = "INSERT INTO proyek (nama_proyek, durasi_pengerjaan) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tfnamaproyek.getText());
                ps.setString(2, tfdurasipengerjaan.getText());
                ps.executeUpdate();

                // Ambil ID yang dihasilkan
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    txt1.setText(String.valueOf(rs.getInt(1)));
                }

                JOptionPane.showMessageDialog(this, "Data Proyek berhasil ditambahkan.");
                loadDataProyek();
                loadComboBoxProyek();
                clearProyekFields();
            } catch (SQLException e) {
                System.out.println("Error Tambah Data Proyek: " + e.getMessage());
            }
        }
         
        //mengupdate data proyek
        private void updateProyek() {
    try {
        int id = Integer.parseInt(txt1.getText());
        
        
        String sqlSelect = "SELECT * FROM proyek WHERE id_proyek = ?";
        PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
        psSelect.setInt(1, id);
        ResultSet rs = psSelect.executeQuery();
        
        String namaProyekLama = "", durasiLama = "";
        if (rs.next()) {
            namaProyekLama = rs.getString("nama_proyek");
            durasiLama = rs.getString("durasi_pengerjaan");
        }
        
        
        String sqlUpdate = "UPDATE proyek SET nama_proyek = ?, durasi_pengerjaan = ? WHERE id_proyek = ?";
        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
        
        
        String namaProyekBaru = tfnamaproyek.getText().isEmpty() ? namaProyekLama : tfnamaproyek.getText();
        String durasiBaru = tfdurasipengerjaan.getText().isEmpty() ? durasiLama : tfdurasipengerjaan.getText();
        
        
        psUpdate.setString(1, namaProyekBaru);
        psUpdate.setString(2, durasiBaru);
        psUpdate.setInt(3, id);
        
       
        psUpdate.executeUpdate();
        
        JOptionPane.showMessageDialog(this, "Data Proyek berhasil diupdate.");
        loadDataProyek();
        loadComboBoxProyek();
        clearProyekFields();
    } catch (SQLException e) {
        System.out.println("Error Update Data Proyek: " + e.getMessage());
    }
}

          
        //menghapus data proyek
        private void hapusProyek() {
            try {
            int id = Integer.parseInt(txt1.getText());

            // Mulai transaksi
            conn.setAutoCommit(false);

            try {
                // Hapus data di tabel proyek
                String sql = "DELETE FROM proyek WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();

                // Reset auto increment dan reorder ID
                String resetSql = "SET @count = 0";
                String updateSql = "UPDATE proyek SET id = @count:= @count + 1";
                String alterSql = "ALTER TABLE proyek AUTO_INCREMENT = 1";

                Statement stmt = conn.createStatement();
                stmt.execute(resetSql);
                stmt.execute(updateSql);
                stmt.execute(alterSql);

                // Commit transaksi
                conn.commit();

                JOptionPane.showMessageDialog(this, "Data Proyek berhasil dihapus.");
                loadDataProyek();
                loadDataTransaksi();
                loadComboBoxProyek();
                clearProyekFields();

            } catch (SQLException ex) {
                // Rollback jika terjadi error
                conn.rollback();
                throw ex;
            } finally {
                // Kembalikan auto commit ke true
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Error Hapus Data Proyek: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID tidak valid");
        }
    }

        
        //Combobox karyawan
        private void loadComboBoxKaryawan() {
          cbkaryawan.removeAllItems();
          try {
            String sql = "SELECT id_karyawan, nama FROM karyawan"; 
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tampilan = String.format("%d - %s", 
                    rs.getInt("id_karyawan"),
                    rs.getString("nama")
                );
                cbkaryawan.addItem(tampilan);
            }
        } catch (SQLException e) {
            System.out.println("Kesalahan Memuat Data Karyawan: " + e.getMessage());
        }
    }

        //Combobox Proyek
        private void loadComboBoxProyek() {
            cbproyek.removeAllItems();
               try {
            String sql = "SELECT id_proyek, nama_proyek FROM proyek"; 
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tampilan = String.format("%d - %s", 
                    rs.getInt("id_proyek"),
                    rs.getString("nama_proyek")
                );
                cbproyek.addItem(tampilan);
            }
        } catch (SQLException e) {
            System.out.println("Kesalahan Memuat Data Proyek: " + e.getMessage());
        }
    }   
        private int getSelectedId(String comboBoxText) {
        if (comboBoxText == null || comboBoxText.isEmpty()) return -1;
        try {
        // Format combo box: "1 - Nama"
        return Integer.parseInt(comboBoxText.split(" - ")[0]);
    } catch (Exception e) {
        System.out.println("Error parsing ID: " + e.getMessage());
        return -1;
    }
}
            
        //menambah data transaksi
        private void tambahTransaksi() {
            try {
            if (cbkaryawan.getSelectedItem() == null || cbproyek.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Pilih karyawan dan proyek terlebih dahulu");
                return;
            }

            String selectedKaryawan = cbkaryawan.getSelectedItem().toString();
            String selectedProyek = cbproyek.getSelectedItem().toString();

            int idKaryawan = getSelectedId(selectedKaryawan);
            int idProyek = getSelectedId(selectedProyek);

            if (idKaryawan == -1 || idProyek == -1) {
                JOptionPane.showMessageDialog(null, "ID Karyawan atau ID Proyek tidak valid");
                return;
            }
            String sql ="INSERT INTO transaksi (id_karyawan, id_proyek, peran) VALUES (?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idKaryawan);
            ps.setInt(2, idProyek);
            ps.setString(3, txtperan.getText().trim());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Transaksi berhasil ditambahkan");
            loadDataTransaksi();
            clearTransaksiFields();

        } catch (SQLException e) {
            System.out.println("Error Tambah Transaksi: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Gagal menambah data: " + e.getMessage());
        }
    }



    
    private void clearTransaksiFields() {
        txtperan.setText("");
        cbkaryawan.setSelectedIndex(0);
        cbproyek.setSelectedIndex(0);
    } 
    
    //mengupdate data transaksi
      private void updateTransaksi() {
         try {
             String sql = "UPDATE transaksi SET peran = ? WHERE id_karyawan = ? AND id_proyek = ?";
             PreparedStatement ps = conn.prepareStatement(sql);

             int idKaryawan = getSelectedId(cbkaryawan.getSelectedItem().toString());
             int idProyek = getSelectedId(cbproyek.getSelectedItem().toString());

             ps.setString(1, txtperan.getText());
             ps.setInt(2, idKaryawan);
             ps.setInt(3, idProyek);
             ps.executeUpdate();
             JOptionPane.showMessageDialog(this, "Data Transaksi berhasil diperbarui");
             loadDataTransaksi();
         } catch (SQLException e) {
             System.out.println("Kesalahan Memperbarui Data Transaksi: " + e.getMessage());
         }
     }

      //menghapus data transaksi
        private void hapusTransaksi() {
         try {
             
             if (cbkaryawan.getSelectedItem() == null || cbproyek.getSelectedItem() == null) {
                 JOptionPane.showMessageDialog(this, "Pilih karyawan dan proyek terlebih dahulu");
                 return;
             }

             
             int idKaryawan = getSelectedId(cbkaryawan.getSelectedItem().toString());
             int idProyek = getSelectedId(cbproyek.getSelectedItem().toString());

             int confirm = JOptionPane.showConfirmDialog(this,
                     "Apakah Anda yakin ingin menghapus data transaksi ini?",
                     "Konfirmasi Hapus",
                     JOptionPane.YES_NO_OPTION);

             if (confirm == JOptionPane.YES_OPTION) {
                 String sql = "DELETE FROM transaksi WHERE id_karyawan = ? AND id_proyek = ?";
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ps.setInt(1, idKaryawan);
                 ps.setInt(2, idProyek);

                 int result = ps.executeUpdate();
                 if (result > 0) {
                     JOptionPane.showMessageDialog(this, "Data Transaksi berhasil dihapus");
                     loadDataTransaksi();
                     clearTransaksiFields();  
                 } else {
                     JOptionPane.showMessageDialog(this, "Data tidak ditemukan atau sudah terhapus");
                 }
             }
         } catch (SQLException e) {
             System.out.println("Kesalahan Menghapus Data Transaksi: " + e.getMessage());
             JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
         }
     }
    
    private void clearKaryawanFields() {
    tfidkaryawan.setText("");
    tfnama.setText("");
    tfjabatan.setText("");
    tfdepartemen.setText("");
}

    private void clearProyekFields() {
        txt1.setText("");
        tfnamaproyek.setText("");
        tfdurasipengerjaan.setText("");
    }

         

 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbjudul = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfidkaryawan = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfnama = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfjabatan = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfdepartemen = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablekaryawan = new javax.swing.JTable();
        btnhapus = new javax.swing.JButton();
        btnupdate = new javax.swing.JButton();
        btntambah = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txt1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tfnamaproyek = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tfdurasipengerjaan = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableproyek = new javax.swing.JTable();
        btnhapusproyek = new javax.swing.JButton();
        btnupdateproyek = new javax.swing.JButton();
        btntambahproyek = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtperan = new javax.swing.JTextField();
        cbkaryawan = new javax.swing.JComboBox<>();
        cbproyek = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabletransaksi = new javax.swing.JTable();
        btnhapustransaksi = new javax.swing.JButton();
        btnupdatetransaksi = new javax.swing.JButton();
        btntambahtransaksi = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(java.awt.Color.lightGray);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbjudul.setBackground(new java.awt.Color(255, 255, 255));
        lbjudul.setFont(new java.awt.Font("Sylfaen", 1, 24)); // NOI18N
        lbjudul.setForeground(new java.awt.Color(51, 51, 51));
        lbjudul.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbjudul.setText("Manajemen Karyawan Dan Proyek");
        lbjudul.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(lbjudul, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 6, 400, 91));

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(java.awt.Color.pink);

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel3.setBackground(new java.awt.Color(255, 51, 255));

        jPanel7.setBackground(new java.awt.Color(255, 0, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Karyawan"));

        jLabel1.setText("ID Karyawan");

        jLabel2.setText("Nama");

        jLabel3.setText("Jabatan");

        jLabel4.setText("Departemen");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                        .addComponent(tfdepartemen, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tfjabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tfnama, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tfidkaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(tfidkaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(tfnama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(tfjabatan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(tfdepartemen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(107, 107, 107))
        );

        tablekaryawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablekaryawan);

        btnhapus.setText("hapus");
        btnhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });

        btnupdate.setText("Update");
        btnupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupdateActionPerformed(evt);
            }
        });

        btntambah.setText("tambah");
        btntambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(btntambah)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnupdate)
                        .addGap(22, 22, 22)
                        .addComponent(btnhapus)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnhapus)
                            .addComponent(btnupdate)
                            .addComponent(btntambah))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Karyawan", jPanel3);

        jPanel6.setBackground(new java.awt.Color(255, 51, 255));

        jPanel8.setBackground(new java.awt.Color(255, 0, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Proyek"));

        jLabel5.setText("ID Proyek");

        jLabel6.setText("Nama Proyek");

        jLabel7.setText("Durasi Pengerjaan");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tfdurasipengerjaan, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfnamaproyek, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(tfnamaproyek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(tfdurasipengerjaan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(147, 147, 147))
        );

        tableproyek.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tableproyek);

        btnhapusproyek.setText("hapus");
        btnhapusproyek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusproyekActionPerformed(evt);
            }
        });

        btnupdateproyek.setText("Update");
        btnupdateproyek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupdateproyekActionPerformed(evt);
            }
        });

        btntambahproyek.setText("Tambah ");
        btntambahproyek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahproyekActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 267, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(btntambahproyek)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnupdateproyek)
                        .addGap(22, 22, 22)
                        .addComponent(btnhapusproyek)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnhapusproyek)
                            .addComponent(btnupdateproyek)
                            .addComponent(btntambahproyek))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 824, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Proyek", jPanel4);

        jPanel11.setBackground(new java.awt.Color(255, 51, 255));

        jPanel12.setBackground(new java.awt.Color(255, 0, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Transaksi Proyek"));

        jLabel11.setText("ID Karyawan");

        jLabel12.setText("ID Proyek");

        jLabel13.setText("Peran");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(29, 29, 29)))
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbkaryawan, 0, 140, Short.MAX_VALUE)
                    .addComponent(cbproyek, 0, 140, Short.MAX_VALUE)
                    .addComponent(txtperan))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(cbkaryawan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cbproyek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtperan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(150, 150, 150))
        );

        tabletransaksi.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tabletransaksi);

        btnhapustransaksi.setText("hapus");
        btnhapustransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapustransaksiActionPerformed(evt);
            }
        });

        btnupdatetransaksi.setText("Update");
        btnupdatetransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnupdatetransaksiActionPerformed(evt);
            }
        });

        btntambahtransaksi.setText("tambah");
        btntambahtransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahtransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(btntambahtransaksi)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnupdatetransaksi)
                        .addGap(22, 22, 22)
                        .addComponent(btnhapustransaksi)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnhapustransaksi)
                            .addComponent(btnupdatetransaksi)
                            .addComponent(btntambahtransaksi))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 824, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 324, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Transaksi Proyek", jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btntambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahActionPerformed
        // TODO add your handling code here:
        tambahKaryawan();
    }//GEN-LAST:event_btntambahActionPerformed

    private void btntambahtransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahtransaksiActionPerformed
        // TODO add your handling code here:
        tambahTransaksi(); 
    }//GEN-LAST:event_btntambahtransaksiActionPerformed

    private void btnupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdateActionPerformed
        // TODO add your handling code here:
         updateKaryawan();
    }//GEN-LAST:event_btnupdateActionPerformed

    private void btnhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusActionPerformed
        // TODO add your handling code here:
         hapusKaryawan();
    }//GEN-LAST:event_btnhapusActionPerformed

    private void btntambahproyekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahproyekActionPerformed
        // TODO add your handling code here:
        tambahProyek();
    }//GEN-LAST:event_btntambahproyekActionPerformed

    private void btnupdateproyekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdateproyekActionPerformed
        // TODO add your handling code here:
        updateProyek();
    }//GEN-LAST:event_btnupdateproyekActionPerformed

    private void btnhapusproyekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusproyekActionPerformed
        // TODO add your handling code here:
        hapusProyek();
    }//GEN-LAST:event_btnhapusproyekActionPerformed

    private void btnupdatetransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnupdatetransaksiActionPerformed
        // TODO add your handling code here:
        updateTransaksi();
    }//GEN-LAST:event_btnupdatetransaksiActionPerformed

    private void btnhapustransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapustransaksiActionPerformed
        // TODO add your handling code here:
        hapusTransaksi();
    }//GEN-LAST:event_btnhapustransaksiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AppKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AppKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AppKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AppKaryawan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AppKaryawan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btnhapusproyek;
    private javax.swing.JButton btnhapustransaksi;
    private javax.swing.JButton btntambah;
    private javax.swing.JButton btntambahproyek;
    private javax.swing.JButton btntambahtransaksi;
    private javax.swing.JButton btnupdate;
    private javax.swing.JButton btnupdateproyek;
    private javax.swing.JButton btnupdatetransaksi;
    private javax.swing.JComboBox<String> cbkaryawan;
    private javax.swing.JComboBox<String> cbproyek;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lbjudul;
    private javax.swing.JTable tablekaryawan;
    private javax.swing.JTable tableproyek;
    private javax.swing.JTable tabletransaksi;
    private javax.swing.JTextField tfdepartemen;
    private javax.swing.JTextField tfdurasipengerjaan;
    private javax.swing.JTextField tfidkaryawan;
    private javax.swing.JTextField tfjabatan;
    private javax.swing.JTextField tfnama;
    private javax.swing.JTextField tfnamaproyek;
    private javax.swing.JTextField txt1;
    private javax.swing.JTextField txtperan;
    // End of variables declaration//GEN-END:variables
}
