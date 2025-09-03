import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Dimensions,
} from "react-native";
import { LinearGradient } from "expo-linear-gradient";

const { width } = Dimensions.get("window");
const scale = (size) => (width / 375) * size;

const ButtonFunction = (props) => {
  const [mainNumbers, setMainNumbers] = useState([]);
  const [luckyStars, setLuckyStars] = useState([]);

  const generateNumbers = () => {
    let newMainNumbers = [];
    let newLuckyStars = [];
    let firstNumbers = props.firstNumbers;
    let firstNumberOfTimes = props.firstNumberOfTimes;
    let secondNumberOfTimes = props.secondNumberOfTimes;
    let secondNumber = props.secondNumber;

    while (newMainNumbers.length < firstNumberOfTimes) {
      let randomNumber = Math.floor(Math.random() * firstNumbers) + 1;
      if (!newMainNumbers.includes(randomNumber)) {
        newMainNumbers.push(randomNumber);
      }
    }
    while (newLuckyStars.length < secondNumberOfTimes) {
      let randomNumber = Math.floor(Math.random() * secondNumber) + 1;
      if (!newLuckyStars.includes(randomNumber)) {
        newLuckyStars.push(randomNumber);
      }
    }
    setMainNumbers(newMainNumbers);
    setLuckyStars(newLuckyStars);
  };

  return (
    <View style={styles.backgroundBox}>
      <View style={styles.numbersContainer}>
        {mainNumbers.map((number, index) => (
          <View key={index} style={styles.numberCircle}>
            <Text style={styles.numberText}>{number}</Text>
          </View>
        ))}
        {luckyStars.map((number, index) => (
          <View key={index} style={styles.luckyNumberCircle}>
            <Text style={styles.luckyNumberText}>{number}</Text>
          </View>
        ))}
      </View>

      <TouchableOpacity onPress={generateNumbers}>
        <LinearGradient
          colors={["#FDC830", "#F37335"]} // Vibrant "Sunny Morning" gradient
          style={styles.button}
        >
          <Text style={styles.buttonText}>{props.buttonTitle}</Text>
        </LinearGradient>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  backgroundBox: {
    marginVertical: scale(12),
    backgroundColor: "rgba(0, 0, 0, 0.2)", // Glassmorphism effect
    alignItems: "center",
    justifyContent: "center",
    minHeight: scale(200),
    width: width * 0.9,
    borderRadius: scale(30),
    padding: scale(20),
  },
  numbersContainer: {
    flexDirection: "row",
    justifyContent: "center",
    flexWrap: "wrap",
    minHeight: scale(55),
    marginBottom: scale(15),
  },
  numberCircle: {
    width: scale(45),
    height: scale(45),
    borderRadius: scale(22.5),
    backgroundColor: "rgba(255, 255, 255, 0.95)", // Glassy white
    alignItems: "center",
    justifyContent: "center",
    margin: scale(4),
    elevation: 4,
  },
  numberText: {
    color: "#F37335", // Vibrant orange accent
    fontWeight: "bold",
    fontSize: scale(20),
  },
  button: {
    paddingVertical: scale(16),
    paddingHorizontal: scale(35),
    borderRadius: scale(25),
    elevation: 5,
  },
  buttonText: {
    textAlign: "center",
    fontSize: scale(18),
    fontWeight: "bold",
    color: "#333", // Dark text for contrast on light button
  },
  luckyNumberText: {
    color: "#FFFFFF", // White text for contrast on orange background
    fontWeight: "bold",
    fontSize: scale(20),
  },
  luckyNumberCircle: {
    width: scale(45),
    height: scale(45),
    borderRadius: scale(22.5),
    backgroundColor: "#F37335", // Vibrant orange accent
    alignItems: "center",
    justifyContent: "center",
    margin: scale(4),
    elevation: 3,
  },
});

export default ButtonFunction;
