package ua.kiev.prog;

import javax.persistence.*;
import java.util.*;


public class Order {
    static EntityManagerFactory emf;
    static EntityManager em;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            emf = Persistence.createEntityManagerFactory("HomeWorkPro7b");
            em = emf.createEntityManager();

            while (true) {
                System.out.println("1: Work with the menu");
                System.out.println("2: Make an order");
                System.out.print("-->");
                String s = sc.nextLine();
                switch (s) {
                    case "1":
                        workWithMenu();
                        break;
                    case "2":
                        makeAnOrder();
                        break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            return;
        } finally {
            sc.close();
            em.close();
            emf.close();
        }
    }

    public static void workWithMenu() {
        while (true) {
            System.out.println("1: Adding a dish to the menu");
            System.out.println("2: Remove dishes from the menu");
            System.out.println("3: View the menu");
            System.out.print("-->");
            String s = sc.nextLine();
            switch (s) {
                case "1":
                    addMenu();
                    break;
                case "2":
                    deleteDish();
                    break;
                case "3":
                    viewMenu();
                    break;
                default:
                    return;
            }
        }
    }

    public static void makeAnOrder() {
        while (true) {
            System.out.println("1: Make an order yourself");
            System.out.println("2: Viewing the menu 'only with a discount'");
            System.out.println("3: View the 'cost from-to' menu");
            System.out.print("-->");
            String s = sc.nextLine();
            switch (s) {
                case "1":
                    makeAnOrderYourself();
                    break;
                case "2":
                    viewMenuDiscount();
                    break;
                case "3":
                    viewMenuPrice();
                    break;
                default:
                    return;
            }
        }
    }

    public static void makeAnOrderYourself() {
        Query query = em.createQuery("SELECT c FROM Menu c", Menu.class);
        List<Menu> list = (List<Menu>) query.getResultList();
        for (Menu menu : list) {
            System.out.println(menu);
        }
        double weight=0;
        List<Menu> order=new ArrayList<>();
        int[] numArr = new int[0];
        try {
            System.out.println("Enter the ID of the dish, separated by a comma (eg 1,2,3)");
            System.out.print("Make your choice -->");
            String s = sc.nextLine();
            numArr = Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException e) {
            System.out.println("Input Error! Repeat, please...");
        }
        try {
            for (int i = 0; i < numArr.length; i++) {
                Menu menu = em.find(Menu.class,numArr[i]);
                if ((weight+menu.getMass())<=1000){
                    weight+=menu.getMass();
                    order.add(menu);
                } else {
                    System.out.println("Total weight is more than 1kg");
                    break;
                }
            }
        } catch (NoResultException e) {
            System.out.println("There is no such dish.");
            return;
        }
        System.out.println("Total weight -> "+weight);
        for (Menu menu : order) {
            System.out.println(menu);
        }
    }

    public static void addMenu() {
        System.out.print("Enter the name of the dish ->");
        String name = sc.nextLine();
        System.out.print("Enter the price of the dish ->");
        String sPrice = sc.nextLine();
        Double price = sPrice.equals("")||sPrice==null?0:Double.parseDouble(sPrice);
        System.out.print("Enter the mass of the dish ->");
        String sMass = sc.nextLine();
        Double mass = sMass.equals("")||sMass==null?0:Double.parseDouble(sMass);
        System.out.print("Enter the discount amount in % for this dish (if there is no discount - press Enter) ->");
        String sDiscount = sc.nextLine();
        Integer discount = sDiscount.equals("")||sDiscount==null?0:Integer.parseInt(sDiscount);
        try {
            em.getTransaction().begin();
            Menu menu = new Menu(name, price, mass, discount);
            em.persist(menu);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    public static void viewMenuDiscount() {
        Query query = em.createQuery("SELECT c FROM Menu c WHERE c.discount>0", Menu.class);
        List<Menu> list = (List<Menu>) query.getResultList();
        for (Menu menu : list) {
            System.out.println(menu);
        }
    }

    public static void viewMenuPrice() {
        System.out.print("Enter price from ->");
        String sPrice=sc.nextLine();
        Double price1=sPrice.equals("")||sPrice==null?0:Double.parseDouble(sPrice);
        System.out.print("Enter price to ->");
        sPrice=sc.nextLine();
        Double price2=sPrice.equals("")||sPrice==null?0:Double.parseDouble(sPrice);
        Query query = em.createQuery("SELECT c FROM Menu c WHERE c.price BETWEEN :price1 AND :price2", Menu.class);
        query.setParameter("price1",price1);
        query.setParameter("price2",price2);
        List<Menu> list = (List<Menu>) query.getResultList();
        for (Menu menu : list) {
            System.out.println(menu);
        }
    }

    public static void viewMenu (){
        Query query = em.createQuery("SELECT c FROM Menu c", Menu.class);
        List<Menu> list = (List<Menu>) query.getResultList();
        for (Menu menu : list) {
            System.out.println(menu);
        }

    }

    public static void deleteDish() {
        viewMenu();
        System.out.print("Enter dish ID ->");
        String sId = sc.nextLine();
        Integer id = Integer.parseInt(sId);
        Menu menu = em.find(Menu.class, id);
        if (menu != null) {
            try {
                em.getTransaction().begin();
                em.remove(menu);
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }
        }
    }
}
