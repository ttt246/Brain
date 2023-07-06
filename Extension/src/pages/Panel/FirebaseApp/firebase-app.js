import { initializeApp } from "firebase/app";
import * as firebaseConfig from "../../../configs/firebase-config.json"

// initialize app
const app = initializeApp(firebaseConfig);
export default app;
