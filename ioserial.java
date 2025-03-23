import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



class Items{
    private String code;
    private double prize;
    private String quantitiy;
    private String manufacturedate;
    private String expiarydate;
    private String productname;


    Items(String code,double prize,String quantitiy,String manufacturedate,String expiarydate,String productname){
        this.code=code;
        this.prize=prize;
        this.quantitiy=quantitiy;
        this.manufacturedate=manufacturedate;
        this.expiarydate=expiarydate;
        this.productname=productname;
    }   

    public void displayDetails(){
        System.out.println("Details: " + "Product: "+ productname + " , prize: "+prize + " , quantity: "+ quantitiy + " , manufacture date: "+ manufacturedate + " , expiary date: "+ expiarydate );
        System.out.println();
    }

    public double getprize(){
        return this.prize;
    }

    public String getdetailstotextfile(){
        return "product: " + productname + "| prize: "+prize; 
    }

    
    
}



public class ioserial{
    private static final String filename = "itms.csv";
    private static Map<String, Items> itemsmap = new HashMap<>();
    private static List<String> pursheditems = new ArrayList<>();
    private static List<String> allcodes = new ArrayList<>();
    private static final String customerfile = "customer.csv";
    
    public static void main(String args[]){
        loadthedetails();
        
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter the cashier name: ");
        String cashiername = scan.nextLine();
        System.out.println();

        System.out.print("Enter the customer name: ");
        String customername = scan.nextLine();
        System.out.println();

        System.out.print("Enter the branch: ");
        String branchname = scan.nextLine();
        System.out.println();


        System.out.println("Is there no items type 0!!");
        System.out.println();

        double grandtotal = 0;

        while(true){

            System.out.print("Enter the code for the item or exit: ");
            String inputcode = scan.nextLine();
            if(!inputcode.equals("0")){
                Items item = itemsmap.get(inputcode);
                if(item!=null){
                    item.displayDetails();
                    pursheditems.add(item.getdetailstotextfile());
                    allcodes.add(inputcode);
                    grandtotal = grandtotal + item.getprize();
                    
                }
                else{
                    System.out.println("The code is wrong!!");
                }
            }
            else{
                break;
            }
        }
        savecustomerdetails(cashiername,customername,branchname,allcodes,grandtotal);
        pdfgenerate(cashiername,customername,branchname,pursheditems,grandtotal);
        
        

    }

    private static void loadthedetails(){
        try(BufferedReader br = new BufferedReader( new FileReader(filename))){
            String line;
            while((line=br.readLine())!=null){
                String[] details = line.split(",");
                String code = details[0];
                double prize = Double.parseDouble(details[1]);
                String quantitiy = details[2];
                String manufacturedate = details[3];
                String expiarydate = details[4];
                String productname = details[5];

                Items item = new Items(code,prize,quantitiy,manufacturedate,expiarydate,productname);
                itemsmap.put(code,item);

            }
        }
        catch(IOException e){
            System.out.println("Loading error!!");
        }
    }

    public static void pdfgenerate(String cashiername, String customername, String branchname, List<String> pursheditems, double grandtotal){
        String filepath = "bill.txt";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))){
            writer.write("======Supermarket Bill====\n\n");
            writer.write("Cashier Name: "+cashiername+ "\n");
            writer.write("Customer Name: "+customername+ "\n");
            writer.write("Branch Name: "+branchname + "\n\n");
            writer.write("---------------------------------\n");

            

            for(String itemdetail: pursheditems){
                writer.write(itemdetail+"\n");
            }


            writer.write("---------------------------------\n");
            writer.write("Total prize: $ "+grandtotal+"\n");
            writer.write("==============================\n");

           
            System.out.print("Bill saved as "+filepath);

        }
        catch(IOException e){
            System.out.println("Error while generating text file: "+e);
        }
    }

    public static void savecustomerdetails(String cashiername, String customername, String branchname, List<String> allcodes, double grandtotal){
        boolean fileexist = new File(customerfile).exists();
        try(BufferedWriter writers = new BufferedWriter(new FileWriter(customerfile,true))){
            if(!fileexist){
                writers.write("cashier,customer,branch,purchased items, total price, timestamp\n");
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writers.write(cashiername+","+customername+","+branchname+","+String.join("|",allcodes)+","+grandtotal+","+timestamp+"\n");

        }
        catch(IOException e){
            System.out.println("Error while saving the customer details!!!");
        }
    }
}


