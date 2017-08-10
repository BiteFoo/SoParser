package com.example;

public class MyClass {

    private static String soPath = "C:\\Users\\John.Lu\\Desktop\\game\\main";
    public static void main(String[] args)
    {

        Utils.log(" ======================== main ================");
        byte[] soinfo = Utils.readElfInfo(soPath);
        if(soinfo != null)
        {
            Parser parser = new Parser(soinfo);
            parser.parseElfHeader();//elf header
            parser.parseElf32_phdr();//e_phdr
            parser.parseSectionHeader();// e_shdr
            parser.addNewSection("C:\\Users\\John.Lu\\Desktop\\game");
        }
    }


}
