import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# === 1. Cargar el CSV ===
df = pd.read_csv("../data/estados_iniciales.csv")

# Asegúrate de que las columnas numéricas están en el tipo correcto
cols_numericas = ["beneficio", "coste", "calidad", "totalKm",
                  "kmMedioCamion", "peticionesAsignadas", "camionesUsados"]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Agrupar por algoritmo ===
resumen = df.groupby("algoritmo").agg({
    "beneficio": "mean",
    "coste": "mean",
    "calidad": "mean",
    "totalKm": "mean",
    "camionesUsados": "mean",
    "peticionesAsignadas": ["mean", "sum"],
}).round(2)

print("Resumen por algoritmo:\n")
print(resumen)
print("\nNúmero de ejecuciones por algoritmo:")
print(df["algoritmo"].value_counts())

# === 3. Visualizaciones ===
sns.set_theme(style="whitegrid")

# --- Beneficio medio por algoritmo ---
plt.figure(figsize=(8,5))
sns.barplot(data=df, x="algoritmo", y="beneficio", estimator="mean", ci="sd", palette="viridis")
plt.title("Beneficio medio por algoritmo")
plt.ylabel("Beneficio medio")
plt.tight_layout()
plt.show()

# --- Coste medio por algoritmo ---
plt.figure(figsize=(8,5))
sns.barplot(data=df, x="algoritmo", y="coste", estimator="mean", ci="sd", palette="magma")
plt.title("Coste medio por algoritmo")
plt.ylabel("Coste medio")
plt.tight_layout()
plt.show()

# --- Calidad media por algoritmo ---
plt.figure(figsize=(8,5))
sns.barplot(data=df, x="algoritmo", y="calidad", estimator="mean", ci="sd", palette="magma")
plt.title("Calidad media por algoritmo")
plt.ylabel("Calidad media")
plt.tight_layout()
plt.show()

# === 4. Alguna estadística adicional ===

# Ratio de peticiones asignadas
df["ratio_asignacion"] = df["peticionesAsignadas"] / df["peticionesTotales"]

# Beneficio por km
df["beneficio_por_km"] = df["beneficio"] / df["totalKm"].replace(0, np.nan)

# Promedios de esas métricas por algoritmo
metricas_extra = df.groupby("algoritmo")[["calidad", "ratio_asignacion", "beneficio_por_km"]].mean().round(3)
print("\nMétricas adicionales por algoritmo:\n")
print(metricas_extra)

# --- Boxplots para ver la variabilidad ---
plt.figure(figsize=(9,5))
sns.boxplot(data=df, x="algoritmo", y="calidad", palette="Spectral")
plt.title("Distribución de la calidad (beneficio - coste)")
plt.tight_layout()
plt.show()

plt.figure(figsize=(9,5))
sns.boxplot(data=df, x="algoritmo", y="ratio_asignacion", palette="cubehelix")
plt.title("Ratio de asignación de peticiones por algoritmo")
plt.tight_layout()
plt.show()

