import { initializeApp } from "firebase/app";

const firebaseConfig = {
    apiKey: "AIzaSyDmaCsRzDgChm7KzZtJ-F4GcS7a5RQagk4",
    authDomain: "test3-83ffc.firebaseapp.com",
    databaseURL: "https://test3-83ffc-default-rtdb.firebaseio.com",
    projectId: "test3-83ffc",
    storageBucket: "test3-83ffc.appspot.com",
    messagingSenderId: "518785483270",
    appId: "1:518785483270:web:fee9f90cc8638814863987",
    measurementId: "G-MEKFXRNHWN"
};

// initialize app
const app = initializeApp(firebaseConfig);
export default app;
