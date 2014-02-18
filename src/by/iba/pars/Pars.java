/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package by.iba.pars;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Deng
 */
public class Pars {

    public static void f(String path){
        File file_in = new File(path);
        File file_out = new File("file_out.txt");
        File file_err = new File("file_err.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file_in));
            String line;
            ArrayList<String> lines = new ArrayList<String>();
            boolean isFormated = true;
            while ((line=br.readLine()) != null) {
                if(isFormated) isFormated = line.matches("[A-z0-9]+=[A-z0-9]+");
                lines.add(line);
            }
            br.close();
            FileWriter wrt = new FileWriter(file_out);
            for (String l : lines) {
                if (isFormated) wrt.write(l.replace("=", "\r\n"));
                else wrt.write(l);
                wrt.write("\r\n");
            }
            wrt.close();
        }
        catch(Exception e){        
            try {
                if(file_err.createNewFile()) e.printStackTrace(new PrintStream(file_err));
            } catch (IOException ex) {
                Logger.getLogger(Pars.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
    
    
    public static void cmd(String CMD){
        File cmd_out = new File("cmd_out.txt");
        File cmd_err = new File("cmd_err.txt");
        try {
            Process pCat = Runtime.getRuntime().exec(new String[]{"cmd","/c",CMD,"\n","exit"});
            InputStream inputStream = pCat.getInputStream();
            InputStream errorStream = pCat.getErrorStream();
            pCat.waitFor();
            BufferedReader outReader = null;
            if(inputStream != null){
                outReader = new BufferedReader(new InputStreamReader(inputStream, "Cp866"));
                FileWriter wrt = new FileWriter(cmd_out);
                String line;
                while ((line = outReader.readLine()) != null) {
                    wrt.append(line+"\r\n");
                }
                wrt.close();
            }
            if(errorStream.available()!=0 ){
                outReader = new BufferedReader(new InputStreamReader(errorStream, "Cp866"));
                FileWriter wrt = new FileWriter(cmd_err);
                String line;
                while ((line = outReader.readLine()) != null) {
                    wrt.append(line+"\r\n");
                }
                wrt.close();
            }      
            outReader.close();
            inputStream.close();
            errorStream.close();          
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void rk(String Path){
        File rk_out = new File("rk_out.txt");
        File rk_err = new File("rk_err.txt");
        try{
            Pattern p = Pattern.compile("^([A-z0-9._\\\\\\s]+)\\\\([A-z0-9._\\s]+)$");
            Matcher m = p.matcher(Path);
            if (m.find()) {
                Process process = Runtime.getRuntime(). exec("reg query \"" + m.group(1) + "\" /v " + m.group(2));
                InputStream inputStream = process.getInputStream();
                InputStream errorStream = process.getErrorStream();
                BufferedReader outReader = new BufferedReader(new InputStreamReader(inputStream, "Cp866"));
                String line;
                FileWriter wrt = new FileWriter(rk_out);
                while ((line = outReader.readLine()) != null) {
                    if( line.contains(m.group(2))){
                        String[] parsed = line.split("    ");
                        wrt.append(m.group(2) + "=" + parsed[parsed.length-1] + "\r\n");
                    }
                }
                wrt.close(); 
                if(errorStream.available()!=0 ){
                    outReader = new BufferedReader(new InputStreamReader(errorStream, "Cp866"));
                    wrt = new FileWriter(rk_err);
                    while ((line = outReader.readLine()) != null) {
                        wrt.append(line+"\r\n");
                    }
                    wrt.close();
                } 
            }
        }
        catch(Exception e){
            try {
                if(rk_err.createNewFile()) e.printStackTrace(new PrintStream(rk_err));
            } catch (IOException ex) {
                Logger.getLogger(Pars.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            if (args[0].equals("-f")) {
                f(args[1]);
            } else if (args[0].equals("-cmd")) {
                cmd(args[1]);
            } else if (args[0].equals("-rk")) {
                rk(args[1]);
            }
        } else {
            System.out.println("Invalid argument.\njava -jar pars.jar [-cmd <path to file>] | [-cmd <command>] | {-rk <path to value>]");
        } 
    }
    
}
