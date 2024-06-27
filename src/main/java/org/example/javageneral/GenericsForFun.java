package org.example.javageneral;

import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericsForFun {

    static final Random RANDOM = new Random();

    static void printList(List<?> list, String context) {
        if( context != null && !context.isEmpty()) {
            System.out.println(context);
        }
        list.stream().forEach(System.out::println);
    }

    /**
     * Prints the animals from the given list.
     * Due to the ? extends Animal generic type any list of Animal or subtypes of Animal can be passed as a list.
     *
     * @param animals the list of animals to print
     */
    static void getAnimalFromList(List<? extends Animal> animals) {
        var sorte = animals.stream()
                .sorted(Comparator.comparing(Animal::getName).thenComparingInt(Animal::getAge))
                .toList();
        for(Animal animal : animals) {
            System.out.println("Animal: %s".formatted(animal));
        }
    }

    /**
     * Prints the dogs from the given list of animals.
     * Only the dogs and its subtypes can be passed as a list.
     *
     * @param animals the list of dogs to print
     */
    static void getDogsFromList(List<? extends Dog> animals) {
        for(Dog dog : animals) {
            System.out.println("Dog: %s".formatted(dog));
        }
    }

    static void addDogToList(List<? super Dog> dogs) {
        dogs.add(new Dog());
        dogs.add(new Poodle());
//        dogs.add(new Animal());
//        dogs.add(new Cat());
        for( Object dog : dogs) {
            switch (dog) {
                case null -> System.out.println("Null");
                case Poodle p -> System.out.println("Poodle: %s".formatted(p));
                case Dog d -> System.out.println("Dog: %s".formatted(d));
                case Animal a -> System.out.println("Animal: %s".formatted(a));
                case Object o -> System.out.println("Object: %s".formatted(o));
            }

        }
    }

    static void addPoddleToList(List<? super Poodle> poodles) {
        poodles.add(new Poodle());
        poodles.add(new Poodle());
//        dogs.add(new Animal());
//        dogs.add(new Dog());
//        dogs.add(new Cat());
    }

    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Dog());
        animals.add(new Cat());
        animals.add(new Poodle());

        List<Dog> dogs = new ArrayList<>();
        dogs.add(new Dog());
        dogs.add(new Poodle());

        List<Poodle> poodles = new ArrayList<>();
        poodles.add(new Poodle());
        poodles.add(new Poodle());


        printList(animals, "Animals");

        getAnimalFromList(animals);
        getDogsFromList(dogs);
        getDogsFromList(poodles);

        addDogToList(animals);
        addDogToList(dogs);
//        addDogToList(poodles);

        addPoddleToList(poodles);

    }

}

@Getter
@Setter
@ToString
class Animal {

    private static final AtomicInteger animalCounter = new AtomicInteger(0);

    private String name = this.getClass().getSimpleName() + animalCounter.getAndIncrement();
    private int age = 2 + GenericsForFun.RANDOM.nextInt(10);

//    @Override
//    public String toString() {
//        return this.getClass().getSimpleName();
//    }
}

class Dog extends Animal { }

class Cat extends Animal { }

class Poodle extends Dog { }