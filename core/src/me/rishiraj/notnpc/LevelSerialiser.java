package me.rishiraj.notnpc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import me.rishiraj.notnpc.entity.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LevelSerialiser {
    public static Level getLevel(int levelNumber) throws Exception {
        Gdx.app.log("LEVEL", "Loading level " + levelNumber);
        FileHandle fileHandle = Gdx.files.internal("level" + levelNumber);
        MyScanner scanner = new MyScanner(fileHandle.reader(10240));
        int enemies = scanner.nextInt();
        int speed = scanner.nextInt();
        List<Person> personList = new ArrayList<>();
        for (int i = 0; i < enemies; ++i) {
            boolean infected = scanner.nextInt() == 1;
            int x = scanner.nextInt() + DisplayConstants.PADDING;
            int y = scanner.nextInt() + DisplayConstants.PADDING;
            float angle = ((float) scanner.nextInt()) * (float) Math.PI / 180.0f;
            float speedX = speed * (float) Math.cos(angle);
            float speedY = speed * (float) Math.sin(angle);
            Person person = new Person(x, y, speedX, speedY, angle, infected);
             Gdx.app.log("PERSON", person.toString() + " added");
            personList.add(person);
        }
        return new Level(personList);
    }

    public static class MyScanner {
        BufferedReader br;
        StringTokenizer st;

        public MyScanner(BufferedReader br) {
            this.br = br;
        }

        String next() {
            while (st == null || !st.hasMoreElements()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }

        double nextDouble() {
            return Double.parseDouble(next());
        }

        String nextLine() {
            String str = "";
            try {
                str = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }

    }
}