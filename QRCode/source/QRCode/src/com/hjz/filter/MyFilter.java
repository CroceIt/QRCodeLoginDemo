package com.hjz.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class MyFilter extends FileFilter{
    private String[] filterString = null;
    public MyFilter(String[] filStrings){
        this.filterString = filStrings;
    }
    public boolean accept(File file){
           if(file.isDirectory()) return true;
           for(int i=0; i<filterString.length; ++i)
               if(file.getName().endsWith(filterString[i]))
                   return true;
           /* ����Ҫ��ʾ���ļ����� */
           /*
            *   File.isDirectory()���Դ˳���·������ʾ���ļ��Ƿ���һ��Ŀ¼
           */
           return false;
      }
      
      public String getDescription() {
          String ss = "";
          for(int i=0; i<filterString.length; ++i)
              ss += " *" + filterString[i];
          return("Txt Files(" + ss + ")");                  //������ʾ�ļ����͵�����
      }
}
