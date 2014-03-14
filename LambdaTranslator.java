
/***************************************************/
/*
 
 Vinay Suri
 COEN 385
 Optional Project - Varient 1
 
 LambdaTranslator.java
 
 Did this project with the guidance of this paper: http://basics.sjtu.edu.cn/~yuxi/papers/lambda_in_pi.pdf
 
*/
/***************************************************/


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;

class LambdaTranslator
{
    
 //   public static Node head = new Node();
    public static String piExp = "";
    public static int ascii = 65;
    
    public static ArrayList<String> Expressions = new ArrayList<String>();
    
    public static void main(String[] args)
    {
        
        //ReadLambdaExpressions("SampleExp.txt");
        //ReadLambdaExpressions("ChurchNumerals.txt");

        
        Console console = System.console();
        String input = "";
        input += console.readLine("Enter a Lambda Expression\nAccepted syntax: \n Use L as the Lambda symbol\n Enter input using the following terms-\n  variable : i.e. x\n  lambda abstraction : i.e. Lx.y\n  function application : i.e. MN\n\n --> ");
        Expressions.add(input);
        
        
        for(int n = 0; n < Expressions.size(); n++)
        {
            piExp = "";
            String Exp = Expressions.get(n);

            Exp = HandleParenthesis(Exp);
            
            Node head = new Node();
            System.out.println("--------------------- \n");
            ascii = 65;
            
            head.Value="_";
            head.Name = (char)ascii;
            ascii++;
            
            //Exp = ReadLambdaExpression("SampleExp.txt");
        
            System.out.println("Successfully read string: " + Exp);
        
            System.out.println("Storing in tree ....");
        
            AddToTree(Exp, head);
        
            ConvertToPiSlides(head);
        
            System.out.println("pi-calculus output:\n" + piExp + "\n"); // Display the string.
            
        }

    }
    
    public static String CheckParen(String Exp)
    {
        String Match;
        
        String pattern = "\\)[a-zA-Z]";
        
        Pattern r = Pattern.compile(pattern);
        
        Matcher m = r.matcher(Exp);
        if (m.find()) {
            //System.out.println(m.group(0));
            Match = m.group(0);
            
        } else {
            //System.out.println("No match found");
            Match = "";
        }
        
        return Match;
        
    }
    
    public static String HandleParenthesis(String Exp)
    {
        String Match= " ";
        
        int ptr=0;
        int endPtr=Exp.length();
        
        
        while(true)
        {
            ptr = Exp.indexOf("(",ptr);
            
            endPtr = Exp.lastIndexOf(")",endPtr);
            
            if(endPtr+2<=Exp.length())
            {
                System.out.println("endptr="+endPtr);
                System.out.println("Substring =" +Exp.substring(endPtr,endPtr+2));
                String temp = CheckParen(Exp.substring(endPtr, endPtr+2));
                if(temp.length()>0)
                {
                    if (ptr > 0) ptr --;
                    String tempExp = Exp.substring(0,ptr) + Exp.charAt(endPtr+1) + Exp.substring(ptr++, endPtr);
                    if(endPtr+2<Exp.length())
                        tempExp+=Exp.substring(endPtr+2, Exp.length());
                    Exp = tempExp+")";
                }
            }
            else {
                endPtr=Exp.lastIndexOf(")",endPtr);
                System.out.println("here");
            }
            System.out.println(Exp);
            
            Match = CheckParen(Exp);
            
            if(Match == "")
                break;
        }
        
        return Exp;
    }

    /*
     Function to read the lambda expression(s) from the file ...
     */
    
    public static void ReadLambdaExpressions(String filename)
    {

        String Exp = "";
        
        BufferedReader br = null;
        
        try {
            String CurrentLine;
            br= new BufferedReader(new FileReader(filename));
        
            while ((CurrentLine = br.readLine()) != null) {
                if(CurrentLine.charAt(0)!='#')
                    Expressions.add(CurrentLine);

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if (br != null) br.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        //return Exp;
    }
    
    // Functions to separate Lambda expression and store in tree
    //
    /*
     
     Example:         (Lx.x5)(Lx.x)
     
                            @
     
                L                       L
     
            x       @               x       x
     
                x       5
     */
    
    
    // Three types
    /*
     Type 1, variable:
     
     x          (x)
     
     Type 2, lambda abstraction:
     
     Lx.M       (Lx)
                 |
                (M)

     Type 3, function application: 
     
     MN             ()
                M        N
     
     
     */
    
    // Just a simple regex to help parse the expression
    
    public static String CheckRegex(String Exp)
    {
        String Match;
        
        String pattern = "L[a-z]\\..|[a-zA-Z][a-zA-Z]|[a-zA-Z]";
        
        Pattern r = Pattern.compile(pattern);
        
        Matcher m = r.matcher(Exp);
        if (m.find()) {
            //System.out.println(m.group(0));
            Match = m.group(0);
            
        } else {
            //System.out.println("No match found");
            Match = "";
        }
        
        return Match;
        
    }
    
    public static void AddToTree(String Exp, Node Current)
    {
        //System.out.println("Expression = "+Exp);
        //Check for expression
        String SubExp = CheckRegex(Exp);
        
        //Add expression to tree based on its category
        //Type 1
        if(SubExp.length()==1)
        {                
            Node tempNode = new Node(Current, (char)ascii, ""+SubExp.charAt(0), 0);
            ascii++;
            
            Current.SetChildRight(tempNode);
            Current = tempNode;

            if(Character.isUpperCase(SubExp.charAt(0)))
                Current.type = 1;
            else
                Current.type = 3;
            
            
            Exp=Exp.replaceFirst(SubExp, " ");
            AddToTree(Exp, Current);
        }
        //Type 2
        else if(SubExp.length()==4 && SubExp.charAt(0)=='L' && SubExp.charAt(2)=='.')
        {
            Node lambdaNode = new Node(Current, (char)ascii, SubExp.substring(0,2), 1);
            ascii++;

            //Node tempNode = new Node(lambdaNode, (char)ascii, ""+SubExp.charAt(3), 0);
            //ascii++;
            
            Current.SetChildLeft(lambdaNode);
            //lambdaNode.SetChildLeft(tempNode);
            
            Current = lambdaNode;
            
            Current.type = 2;
            
            Exp=Exp.replaceFirst(SubExp.substring(0,2), " ");
            AddToTree(Exp, Current);
        }
        
        //Type 3
 /*       else if(SubExp.length()==2)
        {
            Node blankNode = new Node();
            blankNode.SetParent(Current);
            Current.SetChildLeft(blankNode);
            
            blankNode.Name = (char)ascii;
            ascii++;

            Node leftNode = new Node(Current, (char)ascii, ""+SubExp.charAt(0), 1);
            ascii++;
            
            blankNode.SetChildLeft(leftNode);
            blankNode.Value="_";
            //Current = LeftNode;
            
            Node rightNode = new Node(Current, (char)ascii, ""+SubExp.charAt(1), 0);
            ascii++;
            
            blankNode.SetChildRight(rightNode);
            
            Current = leftNode; // tending Left???
            
            Current.
            
            Exp=Exp.replaceFirst(SubExp, " ");
            AddToTree(Exp, Current);
        }*/
        else if (SubExp.length()==2)
        {
            Node tempNode = new Node(Current, (char)ascii, ""+SubExp.charAt(0)+SubExp.charAt(1), 0);
            ascii++;
            
            Current.SetChildRight(tempNode);
            Current = tempNode;
            
            //if(Character.isUpperCase(SubExp.charAt(0)))
            Current.type = 4;
            
            
            Exp=Exp.replaceFirst(SubExp, " ");
            AddToTree(Exp, Current);

        }

    }
    

    // Simple depth first algorithm to print the tree's contents
    
    public static void PrintTree(Node N)
    {
        //Print the value
        
        System.out.println(N.GetValue());
        
        // Get the left child
        if (N.hasLeft) PrintTree(N.GetChildLeft());
        
        // Get the right child
        if (N.hasRight) PrintTree(N.GetChildRight());
        
        return;
        
    }

    // Algorithm to print tree in pi-calculus; use bredth first algorithm for this
    
    /*
     
     The process P is defined as follows,
     
        P := node_name<head,left_child,right_child,value> | P
     
     */
    
    // piExp defined as a global variable above
    
    public static void ConvertToPi(Node N)
    {
        char Parent= '_';
        char LeftChild= '_';
        char RightChild= '_';

        if (N.hasParent) Parent = N.GetParent().Name;

        if (N.hasLeft) LeftChild = N.GetChildLeft().Name;

        if (N.hasRight) RightChild = N.GetChildRight().Name;

        if (piExp.length() > 1) piExp += "| ";
        
        piExp += N.Name + " < " + Parent + ", " + LeftChild + ", " + RightChild + ", " + N.GetValue() +"> ";
        //System.out.println(piExp);
       // System.out.println(N.GetValue());
        if (N.hasLeft) ConvertToPi(N.GetChildLeft());
        if (N.hasRight) ConvertToPi(N.GetChildRight());
        return;
        
    }
    
    /*
     Doing it a different way, using the slide's notation.
     */
    
    public static void ConvertToPiSlides(Node N)
    {
        /*
         
         Some processing goes here ...
         
         Lambda         Pi
         
 1       M              (M)(p)
         
 2       Lx.m           p?x.p?q.[m](q)
         
 3       x              x!p
         
 4       MN             new(a,b).(
                        ([M](a)) |
                        (a!b.a!f) |
                        * ((b?c).[N](c)))
         
         
         */
        
        if (N.type == 1) piExp+=" ("+N.GetValue()+") "+"(p)";
        
        else if (N.type == 2) piExp+="p?"+N.GetValue().charAt(1)+".p?q.[";
        
        else if (N.type == 3) piExp+=N.GetValue()+"!p ";
        
        else if (N.type == 4) piExp+=" new(a,b).((["+ N.GetValue().charAt(0) +"](a)) | (a!b.a!f) | * ((b?c).["+ N.GetValue().charAt(1) +"](c)))";
            
            
        
        if (N.hasLeft) ConvertToPiSlides(N.GetChildLeft());
        
        if (N.hasRight) ConvertToPiSlides(N.GetChildRight());
        
        if(N.type == 2)
            piExp+="](q) ";
        
        
        return;
        
    }
    
}



