# 💚 FitLife — Daily Health Companion

**A simple Android fitness app that tracks your steps, distance, calories and heart rate, calculates how much water you should drink, and reminds you to drink it.**

![Platform](https://img.shields.io/badge/platform-Android-green)
![Language](https://img.shields.io/badge/language-Kotlin-purple)
![Min SDK](https://img.shields.io/badge/minSdk-26-blue)
![Status](https://img.shields.io/badge/status-V1%20MVP-orange)

---

## ✨ Features

| Area | What it does |
|---|---|
| 👣 **Steps** | Daily step count with a progress bar toward your goal |
| 📊 **Hourly activity** | 24-hour steps chart and your most active hour |
| 🚶 **Distance & walking time** | Kilometres covered and minutes walked today |
| 🔥 **Calories** | Estimated active calories burned |
| ❤️ **Heart rate** | Average BPM (when a watch or Health Connect source provides it) |
| 💧 **Water goal** | Calculated from your weight (`weight ÷ 20`) |
| 🥤 **Water log** | Quick add 250 ml / 500 ml, undo last drink |
| ⏰ **Water reminders** | Off, every 1 hour, or every 2 hours (quiet 10 PM – 7 AM) |
| 🥗 **Food guide** | Foods to eat more and foods to limit frequently |
| ⚖️ **BMI** | Calculated from height and weight |
| 👤 **Profile** | Name, age, height, weight, goal, trusted person |

---

## 💧 Water calculation

```
Daily water (L) = weight (kg) / 20      (limited to 1 – 6 L)
```

| Weight | Water per day |
|---|---|
| 20 kg | 1.0 L |
| 40 kg | 2.0 L |
| 60 kg | 3.0 L |
| 80 kg | 4.0 L |

Glasses per day = `goal ÷ 250 ml`. Reminder spacing is spread over the awake window (7 AM – 10 PM).

> ⚠️ This is a **general wellness estimate**, not medical advice. Real needs depend on activity, weather and health conditions.

## ⚖️ BMI calculation

```
BMI = weight (kg) / height (m)²
```

BMI is only a screening measure, not a complete health assessment.

---

## 🧱 Tech stack

- **Language:** Kotlin
- **UI:** XML layouts, Material 3, ViewBinding
- **Architecture:** MVVM-style with a data layer
- **Health data:** [Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect) (steps, distance, active calories, heart rate)
- **Local database:** Room (water log)
- **Background work:** WorkManager (water reminders)
- **Min SDK:** 26 (Android 8.0) | **Target SDK:** 34

## 📁 Project structure

```
app/src/main/java/com/fitlife/app
├── FitLifeApp.kt                 # Notification channel
├── data
│   ├── Db.kt                     # Room: WaterEntity, WaterDao, AppDb
│   ├── HealthConnectManager.kt   # Reads steps, distance, calories, heart rate
│   └── UserStore.kt              # Profile + settings (SharedPreferences)
├── ui
│   ├── OnboardingActivity.kt     # Profile + permission cards
│   ├── MainActivity.kt           # Dashboard
│   ├── WaterActivity.kt          # Water log + reminders
│   ├── FoodActivity.kt           # Food guide
│   ├── ProfileActivity.kt        # Profile, BMI, goals
│   └── PrivacyActivity.kt        # Health Connect privacy rationale
└── util
    ├── Calc.kt                   # Water, BMI, food lists
    └── WaterReminder.kt          # Worker + scheduler
```

## 🔐 Permissions

| Permission | Why |
|---|---|
| `ACTIVITY_RECOGNITION` | Activity access (optional) |
| `POST_NOTIFICATIONS` | Water reminders (optional) |
| `ACCESS_FINE/COARSE_LOCATION` | Reserved for future GPS walking (optional) |
| `health.READ_STEPS` | Daily steps |
| `health.READ_DISTANCE` | Distance walked |
| `health.READ_ACTIVE_CALORIES_BURNED` | Calories |
| `health.READ_HEART_RATE` | Heart rate |

All permissions are requested in context, and the app works with the optional ones declined.

---

## 🚀 Getting started

### Requirements
- Android Studio (latest stable)
- JDK 17
- A device or emulator with **Health Connect**
  - Android 14+: built in
  - Android 13 and below: install *Health Connect* from the Play Store

### Run
```bash
git clone https://github.com/<your-username>/FitLife.git
```
1. Open the folder in Android Studio
2. Let Gradle sync
3. Press **Run ▶**

### Add your own images

Replace the placeholder drawables in `app/src/main/res/drawable/` (delete the `.xml` placeholder, add your `.png` / `.webp` with the same name):

| ImageView ID | Drawable name | Screen |
|---|---|---|
| `ivOnboardingFitness` | `img_onboarding_fitness` | Onboarding |
| `ivHealthyFood` | `img_healthy_food` | Home, Food |
| `ivUnhealthyFood` | `img_unhealthy_food` | Food |
| `ivWater` | `img_water` | Water |

---

## 📸 Screenshots

_Add screenshots here._

| Onboarding | Dashboard | Water | Food |
|---|---|---|---|
| | | | |

---

## ⚠️ Notes & limitations

- Calories, distance and walking time are **estimates**. If Health Connect has no data, values are estimated from steps.
- Heart rate shows only when a wearable or another app writes it to Health Connect.
- FitLife gives general wellness information, **not medical advice**.
- Publishing to Google Play requires a privacy policy and the Health Connect permission declaration in Play Console.

## 🗺️ Roadmap

- [ ] Weekly and monthly reports
- [ ] Streaks and achievements
- [ ] Daily wellness score
- [ ] GPS walking (route, pace, live distance)
- [ ] Food database with calories and nutrition
- [ ] Weight tracking
- [ ] Wear OS support
- [ ] Jetpack Compose migration

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first.

## 📄 License

This project is licensed under the MIT License. Add a `LICENSE` file before publishing.
