package org.server.packets.charTemplates;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class main {

    public static void main(String args[]) throws UnknownHostException {

        Hero h1 = new Hero(3, "A really big name to test things to make sure that the char max is upheldnomatterwhat", 12, 19, 10, 6, InetAddress.getByName("moxie.cs.oswego.edu"), 60402);
        //h1.setAlive(false);
        Villain v1 = new Villain(4, "Super Mega evil monster guy", 120, 190, 1000, 100, InetAddress.getByName("moxie.cs.oswego.edu"), 60402);

        DatagramPacket Villain = v1.getDatagramPacket();
        DatagramPacket Hero = h1.getDatagramPacket();

        Hero h2 = new Hero(Hero, 1);
        Villain v2 = new Villain(Villain, 4);
        v2.setAlive(false);

        System.out.println("Hero Stats");
        System.out.println(h2.getName());
        System.out.println(h2.getId());
        System.out.println(h2.getHealth());
        System.out.println(h2.getAttack());
        System.out.println(h2.getDefense());
        System.out.println(h2.getAmmo());
        System.out.println(h2.getIsAlive());

        System.out.println("\nVillain Stats");
        System.out.println(v2.getName());
        System.out.println(v2.getId());
        System.out.println(v2.getHealth());
        System.out.println(v2.getAttack());
        System.out.println(v2.getDefense());
        System.out.println(v2.getAmmo());
        System.out.println(v2.getIsAlive());


    }
}
