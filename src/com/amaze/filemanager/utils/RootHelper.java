/*
 * Copyright (C) 2014 Arpit Khurana <arpitkh96@gmail.com>
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amaze.filemanager.utils;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RootHelper
{
    public static String runAndWait(String cmd,boolean root)
    {

        CommandCapture cc = new CommandCapture(0, false, cmd);

        try
        {if(root)
            Shell.runRootCommand(cc);
        else
            Shell.runCommand(cc);
        }
        catch (Exception e)
        {
            //       Logger.errorST("Exception when trying to run shell command", e);

            return null;
        }

        if (!waitForCommand(cc))
        {
            return null;
        }

        return cc.toString();
    }
    public static ArrayList<String> runAndWait1(String cmd, final boolean root)
    {
        final ArrayList<String> output=new ArrayList<String>();
Command cc=new Command(1,cmd) {
    @Override
    public void commandOutput(int i, String s) {
        output.add(s);
        System.out.println("output "+root+s);
    }

    @Override
    public void commandTerminated(int i, String s) {

        System.out.println("error"+root+s);

    }

    @Override
    public void commandCompleted(int i, int i2) {

    }
};
        try {
            RootTools.getShell(root).add(cc);
        }
        catch (Exception e)
        {
            //       Logger.errorST("Exception when trying to run shell command", e);
e.printStackTrace();
            return null;
        }

        if (!waitForCommand(cc))
        {
            return null;
        }

        return output;
    }
    private static boolean waitForCommand(Command cmd)
    {
        while (!cmd.isFinished())
        {
            synchronized (cmd)
            {
                try
                {
                    if (!cmd.isFinished())
                    {
                        cmd.wait(2000);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            if (!cmd.isExecuting() && !cmd.isFinished())
            {
                //         Logger.errorST("Error: Command is not executing and is not finished!");
                return false;
            }
        }

        //Logger.debug("Command Finished!");
        return true;
    }


    public static String getCommandLineString(String input) {
        return input.replaceAll(UNIX_ESCAPE_EXPRESSION, "\\\\$1");
    }	private static final String UNIX_ESCAPE_EXPRESSION = "(\\(|\\)|\\[|\\]|\\s|\'|\"|`|\\{|\\}|&|\\\\|\\?)";

    public static ArrayList<String[]> getFilesList(String path,boolean showHidden){
        File f=new File(path);
        ArrayList<String[]> files=new ArrayList<String[]>();
        try {
            if(f.exists() && f.isDirectory()){
                for(File x:f.listFiles()){
                    String k="";
                    if(x.isDirectory())
                        k="-1";
                    if(showHidden){
                        files.add(new String[]{x.getPath(),"",parseFilePermission(x),k});
                    }
                    else{if(!x.isHidden()){files.add(new String[]{x.getPath(),"",parseFilePermission(x),k});}}
                }
            }}catch (Exception e){}


        return files;}
    public static String parseFilePermission(File f){
        String per="";
        if(f.canRead()){per=per+"r";}
        if(f.canWrite()){per=per+"w";}
        if(f.canExecute()){per=per+"x";}
        return  per;}
    public static ArrayList<String[]> getFilesList(String path,boolean root,boolean showHidden)
    {
        String cpath=getCommandLineString(path);
        String p="";
        if(showHidden)p="a ";
        Futils futils=new Futils();
        ArrayList<String[]> a=new ArrayList<String[]>();
        ArrayList<String> ls=new ArrayList<String>();;
        if (root)ls=runAndWait1("ls -l "+p+cpath,root);
        if (ls ==null || ls.size()==0)

        {
            return getFilesList(path,showHidden);
        }
        else {
            for (String file : ls) {

                    try {
                        String[] array=futils.parseName(file);
                        array[0]=path+"/"+array[0];
                        a.add(array);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
            return a;
        }
    }

    public static Integer getCount(File f){
        if(f.exists() && f.canRead() && f.isDirectory()){
            try{return f.listFiles().length;}catch(Exception e){return null;}
        }
        return  null;}}