from pathlib import Path

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np

# Header del CSV
# iteration,seed,time_ms,algorithmEI,op1,op2,op3,op4,nodesExpanded,validInicial,validFinal,heurFunction,heurIni,heurFi,costIni,costFi,dailyProfitIni
# dailyProfitFi,profitIni,profitFi,AssignedPetitionsIni,AssignedPetitionsFi,trucksUsedIni,trucksUsedFi,totalKmIni,totalKmFi


# === 1. Cargar el CSV ===

# --- Configuración ---
outdir = Path("plots/experimento_1")
outdir.mkdir(parents=True, exist_ok=True)

df = pd.read_csv("../data/experimento1_10_100.csv")
# Asegúrate de que las columnas numéricas están en el tipo correcto
cols_numericas = ["time_ms", "nodesExpanded", "heurIni", "heurFi", "costIni", "costFi", "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi", "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi"]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

# === 2. Agrupar por operaciones aplicadas (hay 7 en total) ===
# Definimos if op1 = true: op1 = 'op1' else op1 = ''; etc
# op1 = "SwapPetitions" op2 = "Move/RemovePetitions" op3 = "SwapViajes" op4 = "SwapCamiones"
df["operations"] = df["op1"].astype(str) + df["op2"].astype(str) + df["op3"].astype(str) + df["op4"].astype(str)
df["operations"] = df.apply(lambda row: ''.join([col for col in ["op1", "op2", "op3", "op4"] if row[col]]), axis=1)

# Eliminamos las columnas de operaciones que no nos interesen
# Eliminar filas no deseadas:
bad = {"op3", "op4", "op3op4"}
df = df.loc[~df["operations"].isin(bad)].copy()

# Renombramos (queda feo)
# df["operations"] = df["operations"].str.replace("op1", "SwapPetitions ")
# df["operations"] = df["operations"].str.replace("op2", "Move/RemovePetitions ")
# df["operations"] = df["operations"].str.replace("op3", "SwapViajes ")
# df["operations"] = df["operations"].str.replace("op4", "SwapCamiones ")

df["qualityFi"] = df["profitFi"] - df["costFi"]
df["qualityIni"] = df["profitIni"] - df["costIni"]
cols = [
    "time_ms","nodesExpanded","heurIni","heurFi","costIni","costFi",
    "profitIni","profitFi","dailyProfitIni","dailyProfitFi",
    "qualityIni","qualityFi","AssignedPetitionsIni","AssignedPetitionsFi",
    "trucksUsedFi","totalKmFi"
]

# Visualizar el beneficio final medio por operación y heurística en un heatmap
metrics = ["qualityFi", "costFi", "dailyProfitFi", "profitFi", "nodesExpanded", "time_ms", "AssignedPetitionsFi"]

for metric in metrics:
    tabla = (df.pivot_table(index="operations",
                            columns="heurFunction",
                            values=metric,
                            aggfunc="mean")
             .round(2))

    plt.figure(figsize=(1.2*len(tabla.columns)+4, 0.6*len(tabla.index)+2))
    ax = sns.heatmap(tabla, annot=True, fmt=".2f", linewidths=.5, cmap='coolwarm',cbar=True)
    ax.set_title(f"{metric} por operación y heurística")
    ax.set_xlabel("heuristicFunction")
    ax.set_ylabel("operations")
    plt.yticks(rotation=0)
    plt.tight_layout()
    plt.savefig(outdir / f"heatmap_{metric}.png", dpi=300)

# Visualizar el beneficio final medio por operación y heurística en un gráfico de barras agrupadas
metrics = ["qualityFi", "costFi", "dailyProfitFi", "profitFi", "nodesExpanded", "time_ms", "AssignedPetitionsFi"]

for metric in metrics:
    g = (df.groupby(["operations","heurFunction"])[metric]
         .mean()
         .unstack("heurFunction")
         .round(2))

    fig, ax = plt.subplots(figsize=(1.2*len(g.index)+4, 5))
    x = np.arange(len(g.index))
    width = 0.8 / max(1, len(g.columns))  # ancho por barra

    for i, col in enumerate(g.columns):
        ax.bar(x + i*width, g[col].values, width, label=str(col))

    ax.set_xticks(x + (len(g.columns)-1)*width/2)
    ax.set_xticklabels(g.index, rotation=20)
    ax.set_ylabel(metric)
    ax.set_title(f"{metric} por operación (barras agrupadas por heurística)")
    ax.legend(title="heurFunction", frameon=False)
    plt.tight_layout()
    plt.savefig(outdir / f"aggBars_{metric}.png", dpi=300)

# Variabilidad del incremento en calidad final por combinación de operaciones

#Con heurFunction "Avanzada"
dftemp = df[df["heurFunction"] == "Avanzada"].copy()
dftemp["quality_increment"] = dftemp["qualityFi"] - dftemp["qualityIni"]
plt.figure(figsize=(10,6))
sns.boxplot(data=dftemp, x="operations", y="quality_increment", palette="Set3")
plt.title("Distribución del incremento en calidad final por combinación de operaciones")
plt.ylabel("Incremento en calidad final (qualityFi - qualityIni)")
plt.tight_layout()
plt.savefig(outdir / "boxplot_quality_increment_heurAvanzada.png", dpi=300)

#Con heurFunction "Basica"
dftemp = df[df["heurFunction"] == "Basica"].copy()
dftemp["quality_increment"] = dftemp["qualityFi"] - dftemp["qualityIni"]
plt.figure(figsize=(10,6))
sns.boxplot(data=dftemp, x="operations", y="quality_increment", palette="Set3")
plt.title("Distribución del incremento en calidad final por combinación de operaciones")
plt.ylabel("Incremento en calidad final (qualityFi - qualityIni)")
plt.tight_layout()
plt.savefig(outdir / "boxplot_quality_increment_heurBasica.png", dpi=300)



