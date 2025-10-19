import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

# === Asegurarse de que existe la carpeta de salida ===
os.makedirs("data/plots", exist_ok=True)

# === Cargar el CSV ===
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

# === Calcular medias agrupadas por tipo de inicialización ===
resumen = df.groupby("initial").agg({
    "timeMs": "mean",
    "nodesExpanded": "mean",
    "initialHeur": "mean",
    "finalHeur": "mean",
    "profitIni": "mean",
    "profitFi": "mean",
    "costIni": "mean",
    "costFi": "mean"
}).round(2)

print("\n📊 RESUMEN ESTADÍSTICO POR TIPO DE INICIALIZADOR:")
print(resumen)
print("\n====================================================\n")

# === Configuración visual ===
sns.set_theme(style="whitegrid", palette="muted")

# --- Heurística final ---
plt.figure(figsize=(9,5))
sns.barplot(data=df, x="initial", y="finalHeur", hue="initial", legend=False, errorbar="sd")
plt.title("Heurística final por tipo de inicializador (menor = mejor)")
plt.ylabel("Valor heurístico final")
plt.xticks(rotation=30)
plt.tight_layout()
plt.savefig("data/plots/heuristica_final.png", dpi=300)
plt.show()

# --- Beneficio final ---
plt.figure(figsize=(9,5))
sns.barplot(data=df, x="initial", y="profitFi", hue="initial", legend=False, errorbar="sd", palette="viridis")
plt.title("Beneficio medio final por inicializador (mayor = mejor)")
plt.ylabel("Beneficio medio final")
plt.xticks(rotation=30)
plt.tight_layout()
plt.savefig("data/plots/beneficio_final.png", dpi=300)
plt.show()

# --- Tiempo medio ---
plt.figure(figsize=(9,5))
sns.barplot(data=df, x="initial", y="timeMs", hue="initial", legend=False, errorbar="sd", palette="magma")
plt.title("Tiempo medio de ejecución por inicializador (ms)")
plt.ylabel("Tiempo (ms)")
plt.xticks(rotation=30)
plt.tight_layout()
plt.savefig("data/plots/tiempo_medio.png", dpi=300)
plt.show()

# --- Nodos expandidos ---
plt.figure(figsize=(9,5))
sns.barplot(data=df, x="initial", y="nodesExpanded", hue="initial", legend=False, errorbar="sd", palette="crest")
plt.title("Nodos expandidos por inicializador")
plt.ylabel("Nodos expandidos")
plt.xticks(rotation=30)
plt.tight_layout()
plt.savefig("data/plots/nodos_expandidos.png", dpi=300)
plt.show()

print("✅ Gráficas guardadas en: data/plots/")

