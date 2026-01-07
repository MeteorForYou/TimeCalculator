package com.example.timecalculator.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.regex.Pattern

/**
 * 时间解析工具类 - 用正则表达式从文本中提取时间
 */
object TimeParser {
    // 时间格式的正则表达式
    private val TIME_PATTERNS = listOf(
        // HH:mm:ss 格式 (如 14:30:00)
        Pattern.compile("(\\d{1,2}):(\\d{2}):(\\d{2})"),
        // HH:mm 格式 (如 14:30 或 9:05)
        Pattern.compile("(\\d{1,2}):(\\d{2})(?!:)"),
        // 中文格式 (如 14时30分、9点05分)
        Pattern.compile("(\\d{1,2})[时点](\\d{1,2})分?"),
        // 上午/下午格式 (如 上午9:30、下午2:30)
        Pattern.compile("(上午|下午|AM|PM|am|pm)\\s*(\\d{1,2}):(\\d{2})"),
        // 带日期的格式 yyyy-MM-dd HH:mm
        Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})\\s+(\\d{1,2}):(\\d{2})")
    )

    data class ParsedTime(
        val timeMillis: Long,
        val displayText: String,
        val startIndex: Int,
        val endIndex: Int
    )

    /**
     * 从文本中解析所有时间
     * @param text 要解析的文本
     * @param baseDate 基准日期，默认为今天
     * @return 解析出的时间列表
     */
    fun parseTimesFromText(text: String, baseDate: LocalDate = LocalDate.now()): List<ParsedTime> {
        val results = mutableListOf<ParsedTime>()
        val usedRanges = mutableListOf<IntRange>()

        // 先匹配带日期的完整格式
        val dateTimePattern = TIME_PATTERNS[4]
        val dateTimeMatcher = dateTimePattern.matcher(text)
        while (dateTimeMatcher.find()) {
            val range = dateTimeMatcher.start()..dateTimeMatcher.end()
            if (usedRanges.none { it.overlaps(range) }) {
                try {
                    val year = dateTimeMatcher.group(1)!!.toInt()
                    val month = dateTimeMatcher.group(2)!!.toInt()
                    val day = dateTimeMatcher.group(3)!!.toInt()
                    val hour = dateTimeMatcher.group(4)!!.toInt()
                    val minute = dateTimeMatcher.group(5)!!.toInt()

                    if (isValidDateTime(year, month, day, hour, minute)) {
                        val dateTime = LocalDateTime.of(year, month, day, hour, minute)
                        val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val displayText = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

                        results.add(ParsedTime(millis, displayText, dateTimeMatcher.start(), dateTimeMatcher.end()))
                        usedRanges.add(range)
                    }
                } catch (e: Exception) {
                    // 忽略解析失败
                }
            }
        }

        // 匹配上午/下午格式
        val amPmPattern = TIME_PATTERNS[3]
        val amPmMatcher = amPmPattern.matcher(text)
        while (amPmMatcher.find()) {
            val range = amPmMatcher.start()..amPmMatcher.end()
            if (usedRanges.none { it.overlaps(range) }) {
                try {
                    val amPm = amPmMatcher.group(1)!!
                    var hour = amPmMatcher.group(2)!!.toInt()
                    val minute = amPmMatcher.group(3)!!.toInt()

                    // 转换为24小时制
                    if ((amPm == "下午" || amPm.equals("PM", ignoreCase = true)) && hour < 12) {
                        hour += 12
                    } else if ((amPm == "上午" || amPm.equals("AM", ignoreCase = true)) && hour == 12) {
                        hour = 0
                    }

                    if (isValidTime(hour, minute)) {
                        val time = LocalTime.of(hour, minute)
                        val dateTime = LocalDateTime.of(baseDate, time)
                        val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val displayText = time.format(DateTimeFormatter.ofPattern("HH:mm"))

                        results.add(ParsedTime(millis, displayText, amPmMatcher.start(), amPmMatcher.end()))
                        usedRanges.add(range)
                    }
                } catch (e: Exception) {
                    // 忽略解析失败
                }
            }
        }

        // 匹配 HH:mm:ss 格式
        val hmsPattern = TIME_PATTERNS[0]
        val hmsMatcher = hmsPattern.matcher(text)
        while (hmsMatcher.find()) {
            val range = hmsMatcher.start()..hmsMatcher.end()
            if (usedRanges.none { it.overlaps(range) }) {
                try {
                    val hour = hmsMatcher.group(1)!!.toInt()
                    val minute = hmsMatcher.group(2)!!.toInt()
                    val second = hmsMatcher.group(3)!!.toInt()

                    if (isValidTime(hour, minute, second)) {
                        val time = LocalTime.of(hour, minute, second)
                        val dateTime = LocalDateTime.of(baseDate, time)
                        val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val displayText = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                        results.add(ParsedTime(millis, displayText, hmsMatcher.start(), hmsMatcher.end()))
                        usedRanges.add(range)
                    }
                } catch (e: Exception) {
                    // 忽略解析失败
                }
            }
        }

        // 匹配 HH:mm 格式
        val hmPattern = TIME_PATTERNS[1]
        val hmMatcher = hmPattern.matcher(text)
        while (hmMatcher.find()) {
            val range = hmMatcher.start()..hmMatcher.end()
            if (usedRanges.none { it.overlaps(range) }) {
                try {
                    val hour = hmMatcher.group(1)!!.toInt()
                    val minute = hmMatcher.group(2)!!.toInt()

                    if (isValidTime(hour, minute)) {
                        val time = LocalTime.of(hour, minute)
                        val dateTime = LocalDateTime.of(baseDate, time)
                        val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val displayText = time.format(DateTimeFormatter.ofPattern("HH:mm"))

                        results.add(ParsedTime(millis, displayText, hmMatcher.start(), hmMatcher.end()))
                        usedRanges.add(range)
                    }
                } catch (e: Exception) {
                    // 忽略解析失败
                }
            }
        }

        // 匹配中文格式
        val chinesePattern = TIME_PATTERNS[2]
        val chineseMatcher = chinesePattern.matcher(text)
        while (chineseMatcher.find()) {
            val range = chineseMatcher.start()..chineseMatcher.end()
            if (usedRanges.none { it.overlaps(range) }) {
                try {
                    val hour = chineseMatcher.group(1)!!.toInt()
                    val minute = chineseMatcher.group(2)!!.toInt()

                    if (isValidTime(hour, minute)) {
                        val time = LocalTime.of(hour, minute)
                        val dateTime = LocalDateTime.of(baseDate, time)
                        val millis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        val displayText = time.format(DateTimeFormatter.ofPattern("HH:mm"))

                        results.add(ParsedTime(millis, displayText, chineseMatcher.start(), chineseMatcher.end()))
                        usedRanges.add(range)
                    }
                } catch (e: Exception) {
                    // 忽略解析失败
                }
            }
        }

        // 按在文本中出现的位置排序
        return results.sortedBy { it.startIndex }
    }

    /**
     * 计算两个时间之间的差值（分钟）
     */
    fun calculateDurationMinutes(startMillis: Long, endMillis: Long): Long {
        return (endMillis - startMillis) / (1000 * 60)
    }

    /**
     * 格式化时长为可读文本
     */
    fun formatDuration(totalMinutes: Long): String {
        val isNegative = totalMinutes < 0
        val absMinutes = kotlin.math.abs(totalMinutes)

        val days = absMinutes / (24 * 60)
        val hours = (absMinutes % (24 * 60)) / 60
        val minutes = absMinutes % 60

        return buildString {
            if (isNegative) append("-")
            if (days > 0) append("${days}天")
            if (hours > 0 || days > 0) append("${hours}小时")
            append("${minutes}分钟")
        }
    }

    /**
     * 格式化时长为详细文本
     */
    fun formatDurationDetailed(totalMinutes: Long): String {
        val isNegative = totalMinutes < 0
        val absMinutes = kotlin.math.abs(totalMinutes)

        val days = absMinutes / (24 * 60)
        val hours = (absMinutes % (24 * 60)) / 60
        val minutes = absMinutes % 60

        return buildString {
            if (isNegative) append("- ")
            if (days > 0) append("${days}天 ")
            if (hours > 0 || days > 0) append("${hours}小时 ")
            append("${minutes}分钟")
            append("\n\n")
            append("共计 ${if (isNegative) "-" else ""}${absMinutes} 分钟")
            append("\n")
            append("共计 ${if (isNegative) "-" else ""}${String.format(Locale.US, "%.2f", absMinutes / 60.0)} 小时")
        }
    }

    private fun isValidTime(hour: Int, minute: Int, second: Int = 0): Boolean {
        return hour in 0..23 && minute in 0..59 && second in 0..59
    }

    private fun isValidDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int): Boolean {
        if (year < 1900 || year > 2100) return false
        if (month < 1 || month > 12) return false
        if (day < 1 || day > 31) return false
        return isValidTime(hour, minute)
    }

    private fun IntRange.overlaps(other: IntRange): Boolean {
        return this.first < other.last && other.first < this.last
    }
}