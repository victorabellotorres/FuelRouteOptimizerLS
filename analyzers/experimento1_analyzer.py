import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# Header del CSV
# iteration,seed,time_ms,op1,op2,op3,nodesExpanded,validInicial,validFinal,heurIni,heurFi,costIni,costFi,
# profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi


# === 1. Cargar el CSV ===

df = pd.read_csv("../data/experimento1.csv")
# Asegúrate de que las columnas numéricas están en el tipo correcto
cols_numericas = ["time_ms", "nodesExpanded", "costIni", "costFi",
                  "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi",
                  "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi"]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Agrupar por operaciones aplicadas (hay 7 en total) ===
df["operations"] = df["op1"].astype(str) + df["op2"].astype(str) + df["op3"].astype(str)
resumen = df.groupby("operations").agg({
    "time_ms": "mean",
    "nodesExpanded": "mean",
    "costFi": "mean",
    "costIni": "mean",
    "profitIni": "mean",
    "profitFi": "mean",
    "AssignedPetitionsIni": "mean",
    "AssignedPetitionsFi": "mean",
    "trucksUsedFi": "mean",
    "totalKmFi": "mean",
    "heurIni": "mean",
    "heurFi": "mean",
}).round(2)

print("Resumen por combinación de operaciones:\n")
print(resumen)

print("\nNúmero de ejecuciones por combinación de operaciones:")
print(df["operations"].value_counts())

# === 3. Visualizaciones ===
sns.set_theme(style="whitegrid")
# --- Tiempo medio por combinación de operaciones ---
plt.figure(figsize=(10,6))
sns.barplot(data=df, x="operations", y="time_ms", estimator="mean", ci="sd", palette="viridis")
plt.title("Tiempo medio por combinación de operaciones")
plt.ylabel("Tiempo medio (ms)")
plt.tight_layout()
plt.show()
# --- Beneficio medio por combinación de operaciones ---
plt.figure(figsize=(10,6))
sns.barplot(data=df, x="operations", y="profitFi", estimator="mean", ci="sd", palette="magma")
plt.title("Beneficio medio por combinación de operaciones")
plt.ylabel("Beneficio medio")
plt.tight_layout()
plt.show()
# --- Coste medio por combinación de operaciones ---
plt.figure(figsize=(10,6))
sns.barplot(data=df, x="operations", y="costFi", estimator="mean", ci="sd", palette="magma")
plt.title("Coste medio por combinación de operaciones")
plt.ylabel("Coste medio")
plt.tight_layout()
plt.show()
# === 4. Alguna estadística adicional ===
# Ratio de peticiones asignadas finales
df["ratio_asignacion_final"] = df["AssignedPetitionsFi"] / df["AssignedPetitionsIni"]
resumen_ratio = df.groupby("operations")["ratio_asignacion_final"].mean().round(2)
print("\nRatio medio de peticiones asignadas finales por combinación de operaciones:\n")
print(resumen_ratio)
# Visualización del ratio de peticiones asignadas finales
# plt.figure(figsize=(10,6))
# sns.barplot(data=df, x="operations", y="ratio_asignacion_final", estimator="mean", ci="sd", palette="coolwarm")
# plt.title("Ratio medio de peticiones asignadas finales por combinación de operaciones")
# plt.ylabel("Ratio medio de peticiones asignadas finales")
# plt.tight_layout()
# plt.show()

# --- Boxplots para ver el incremento de calidad de beneficio ---
# df["incremento_beneficio"] = (df["profitFi"] - df["costIni"]) - (df["profitIni"] - df["costIni"])
# plt.figure(figsize=(10,6))
# sns.boxplot(data=df, x="operations", y="incremento_beneficio", palette="Set3")
# plt.title("Distribución del incremento en beneficio por combinación de operaciones")
# plt.ylabel("Incremento en beneficio (CalidadFi - calidadFinal)")
# --- Boxplots para ver el incremento de la heurística ---
df["incremento_heuristica"] = -(df["heurFi"] - df["heurIni"])
plt.figure(figsize=(10,6))
sns.boxplot(data=df, x="operations", y="incremento_heuristica", palette="Set3")
plt.title("Distribución del incremento en beneficio por combinación de operaciones")
plt.ylabel("Incremento en beneficio (profitFi - profitIni)")
plt.show()
#Visualizar los nodos expandidos
plt.figure(figsize=(10,6))
sns.boxplot(data=df, x="operations", y="nodesExpanded", palette="Set2")
plt.title("Distribución de nodos expandidos por combinación de operaciones")
plt.ylabel("Nodos expandidos")
plt.show()


