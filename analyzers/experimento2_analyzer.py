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
outdir = Path("plots/experimento_2")
outdir.mkdir(parents=True, exist_ok=True)

df = pd.read_csv("../data/experimento2_10_100.csv")
# Asegúrate de que las columnas numéricas están en el tipo correcto
cols_numericas = ["time_ms", "nodesExpanded", "heurIni", "heurFi", "costIni", "costFi", "profitIni", "profitFi", "AssignedPetitionsIni", "AssignedPetitionsFi", "trucksUsedIni", "trucksUsedFi", "totalKmIni", "totalKmFi"]
df[cols_numericas] = df[cols_numericas].apply(pd.to_numeric, errors="coerce")

print(df["algorithmEI"].value_counts())

df["qualityFi"] = df["profitFi"] - df["costFi"]
df["qualityIni"] = df["profitIni"] - df["costIni"]

#Definimos metricas derivadas
df["deltaQualityFi"] = df["qualityFi"] - df["qualityIni"]
df["deltaHeurFi"] = df["heurFi"] - df["heurIni"]
df["deltaCostFi"] = df["costFi"] - df["costIni"]
df["deltaDailyProfitFi"] = df["dailyProfitFi"] - df["dailyProfitIni"]
df["deltaProfitFi"] = df["profitFi"] - df["profitIni"]
df["deltaAssignedPetitionsFi"] = df["AssignedPetitionsFi"] - df["AssignedPetitionsIni"]


# === 2. Visualizaciones ===

sns.set_theme(style="whitegrid")

# Bar plots para métricas clave
metrics = ["qualityFi", "heurFi", "costFi", "dailyProfitFi", "profitFi", "nodesExpanded", "time_ms", "AssignedPetitionsFi"]
for metric in metrics:
    plt.figure(figsize=(9,5))
    sns.barplot(data=df, x="algorithmEI", y=metric, estimator="mean", ci=95)  # ci="sd" si prefieres desviación
    plt.title(f"{metric} medio por estrategia de inicialización")
    plt.xlabel("algorithmEI")
    plt.ylabel(metric)
    plt.tight_layout()
    plt.savefig(outdir / f"barplot_{metric}.png", dpi=300)


metrics = ["qualityFi", "heurFi", "costFi", "dailyProfitFi", "profitFi", "nodesExpanded", "time_ms", "AssignedPetitionsFi"]
for metric in metrics:
    gmean = (df.groupby(["iteration","algorithmEI"])[metric]
             .mean()
             .reset_index())

    winners_per_op = (gmean.loc[gmean.groupby("iteration")[metric].idxmax()]
    .set_index("iteration")["algorithmEI"])

    win_rate_macro = (winners_per_op.value_counts(normalize=True)*100).round(1)
    print("Win-rate (macro por operación):\n", win_rate_macro)

    # plot
    import matplotlib.pyplot as plt
    win_rate_macro.plot(kind="bar", figsize=(7,4))
    plt.title(f"Ganador por operación (métrica: {metric})")
    plt.ylabel("% operaciones ganadas"); plt.xlabel("algorithmEI")
    plt.tight_layout()
    plt.savefig(outdir / f"winrate_macro_{metric}.png", dpi=300)

delta_metrics = ["deltaQualityFi", "deltaHeurFi", "deltaCostFi", "deltaDailyProfitFi", "deltaProfitFi", "deltaAssignedPetitionsFi"]
for metric in delta_metrics:
    plt.figure(figsize=(9,5))
    sns.boxplot(data=df, x="algorithmEI", y=metric, showfliers=True)
    # (opcional) puntos individuales encima del boxplot:
    sns.stripplot(data=df, x="algorithmEI", y=metric, size=3, alpha=0.35, color="k")

    plt.title(f"Distribución de {metric} por estrategia de inicialización")
    plt.xlabel("algorithmEI")
    plt.ylabel(metric)
    plt.tight_layout()
    plt.savefig(outdir / f"boxplot_{metric}.png", dpi=300)

# Violin plot para distribución completa de un métrico específico
# metric = "profitFi"

# plt.figure(figsize=(10,5))
# sns.violinplot(data=df, x="algorithmEI", y=metric, inner=None)
# sns.stripplot(data=df, x="algorithmEI", y=metric, size=3, alpha=0.5)
# plt.title(f"Distribución de {metric} por algorithmEI")
# plt.xlabel("algorithmEI")
# plt.ylabel(metric)
# plt.tight_layout()
# plt.show()
# # === Calcular métricas derivadas ===
# df["deltaHeur"] = df["finalHeur"] - df["initialHeur"]
#
# # ===  Resumen general ===
# resumen = df.groupby("initial").agg({
#     "initialHeur": "mean",
#     "finalHeur": "mean",
#     "deltaHeur": "mean",
#     "profitFi": "mean",
#     "timeMs": "mean"
# }).round(2)
#
# print("\n RESUMEN ESTADÍSTICO (medias por inicializador):")
# print(resumen)
# print("\nRecuerda: menor heurística = mejor. Δ negativo = mejora.\n")
# print("====================================================\n")
#
# # === Estilo visual ===
# sns.set_theme(style="whitegrid", palette="muted")
#
# # ===  Boxplot 1: Heurística final (calidad final del estado) ===
# plt.figure(figsize=(9,6))
# sns.boxplot(data=df, x="initial", y="finalHeur", palette="coolwarm")
# plt.title("Heurística final por inicializador (menor = mejor calidad de solución)")
# plt.ylabel("Heurística final")
# plt.xlabel("Método de inicialización")
# plt.xticks(rotation=20)
# plt.tight_layout()
# plt.savefig("data/plots/1_boxplot_heuristica_final.png", dpi=300)
# plt.show()
#
# # === Boxplot 2: Mejora de heurística (Δ) ===
# plt.figure(figsize=(9,6))
# sns.boxplot(data=df, x="initial", y="deltaHeur", palette="RdBu_r")
# plt.title("Mejora en heurística (Δ = heurFinal - heurInicial)")
# plt.axhline(0, color="gray", linestyle="--", linewidth=1)
# plt.ylabel("Cambio en heurística (Δ) — más negativo = mayor mejora")
# plt.xlabel("Método de inicialización")
# plt.xticks(rotation=20)
# plt.tight_layout()
# plt.savefig("data/plots/2_boxplot_delta_heuristica.png", dpi=300)
# plt.show()
#
# # ===  Boxplot 3: Beneficio final ===
# plt.figure(figsize=(9,6))
# sns.boxplot(data=df, x="initial", y="profitFi", palette="viridis")
# plt.title("Beneficio final por inicializador (mayor = mejor rendimiento económico)")
# plt.ylabel("Beneficio final")
# plt.xlabel("Método de inicialización")
# plt.xticks(rotation=20)
# plt.tight_layout()
# plt.savefig("data/plots/3_boxplot_beneficio_final.png", dpi=300)
# plt.show()
#
# # ===  Boxplot 4: Tiempo de ejecución ===
# plt.figure(figsize=(9,6))
# sns.boxplot(data=df, x="initial", y="timeMs", palette="magma")
# plt.title("Tiempo de ejecución por inicializador (menor = más eficiente)")
# plt.ylabel("Tiempo (ms)")
# plt.xlabel("Método de inicialización")
# plt.xticks(rotation=20)
# plt.tight_layout()
# plt.savefig("data/plots/4_boxplot_tiempo.png", dpi=300)
# plt.show()
#
# print("✅ Boxplots generados correctamente en: data/plots/")
