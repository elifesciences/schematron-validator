<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@context">
        <xsl:attribute name="context">
            <xsl:call-template name="replace-element-citation">
                <xsl:with-param name="text" select="."/>
            </xsl:call-template>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="node()/text()">
        <xsl:call-template name="replace-element-citation">
            <xsl:with-param name="text" select="."/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="replace-element-citation">
        <xsl:param name="text"/>
        <xsl:choose>
            <xsl:when test="contains($text,'element-citation')">
                <xsl:value-of select="substring-before($text,'element-citation')"/>
                <xsl:value-of select="'mixed-citation'"/>
                <xsl:call-template name="replace-element-citation">
                    <xsl:with-param name="text"
                                    select="substring-after($text,'element-citation')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="xsl:value-of">
        <value-of><xsl:apply-templates select="@*|node()" /></value-of>
    </xsl:template>
</xsl:stylesheet>
