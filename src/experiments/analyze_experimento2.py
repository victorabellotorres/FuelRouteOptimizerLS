import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

# === Crear carpeta de salida ===
os.makedirs("data/plots", exist_ok=True)

# === Cargar datos ===
df = pd.read_csv("data/experimento2.csv")

# === Convertir columnas numéricas ===
cols_numericas = [
    "timeMs", "nodesExpanded",
    "costIni", "costFi",
    "profitIni", "profitFi",
    "initialHeur", "finalHeur"
]
for col in cols_numericas:
    if col in df.columns:
        df[col] = pd.to_numeric(df[col], errors="coerce")

# === Calcular métricas derivadas ===
df["deltaHeur"] = df["finalHeur"] - df["initialHeur"]

# ===  Resumen general ===
resumen = df.groupby("initial").agg({
    "initialHeur": "mean",
    "finalHeur": "mean",
    "deltaHeur": "mean",
    "profitFi": "mean",
    "timeMs": "mean"
}).round(2)

print("\n RESUMEN ESTADÍSTICO (medias por inicializador):")
print(resumen)
print("\nRecuerda: menor heurística = mejor. Δ negativo = mejora.\n")
print("====================================================\n")

# === Estilo visual ===
sns.set_theme(style="whitegrid", palette="muted")

# ===  Boxplot 1: Heurística final (calidad final del estado) ===
plt.figure(figsize=(9,6))
sns.boxplot(data=df, x="initial", y="finalHeur", palette="coolwarm")
plt.title("Heurística final por inicializador (menor = mejor calidad de solución)")
plt.ylabel("Heurística final")
plt.xlabel("Método de inicialización")
plt.xticks(rotation=20)
plt.tight_layout()
plt.savefig("data/plots/1_boxplot_heuristica_final.png", dpi=300)
plt.show()

# === Boxplot 2: Mejora de heurística (Δ) ===
plt.figure(figsize=(9,6))
sns.boxplot(data=df, x="initial", y="deltaHeur", palette="RdBu_r")
plt.title("Mejora en heurística (Δ = heurFinal - heurInicial)")
plt.axhline(0, color="gray", linestyle="--", linewidth=1)
plt.ylabel("Cambio en heurística (Δ) — más negativo = mayor mejora")
plt.xlabel("Método de inicialización")
plt.xticks(rotation=20)
plt.tight_layout()
plt.savefig("data/plots/2_boxplot_delta_heuristica.png", dpi=300)
plt.show()

# ===  Boxplot 3: Beneficio final ===
plt.figure(figsize=(9,6))
sns.boxplot(data=df, x="initial", y="profitFi", palette="viridis")
plt.title("Beneficio final por inicializador (mayor = mejor rendimiento económico)")
plt.ylabel("Beneficio final")
plt.xlabel("Método de inicialización")
plt.xticks(rotation=20)
plt.tight_layout()
plt.savefig("data/plots/3_boxplot_beneficio_final.png", dpi=300)
plt.show()

# ===  Boxplot 4: Tiempo de ejecución ===
plt.figure(figsize=(9,6))
sns.boxplot(data=df, x="initial", y="timeMs", palette="magma")
plt.title("Tiempo de ejecución por inicializador (menor = más eficiente)")
plt.ylabel("Tiempo (ms)")
plt.xlabel("Método de inicialización")
plt.xticks(rotation=20)
plt.tight_layout()
plt.savefig("data/plots/4_boxplot_tiempo.png", dpi=300)
plt.show()

print("✅ Boxplots generados correctamente en: data/plots/")
